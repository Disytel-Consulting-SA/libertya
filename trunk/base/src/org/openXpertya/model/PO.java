/*****************************************************************************************************************************************
 *     El contenido de este fichero est� sujeto a la Licencia P�blica openXpertya versi�n 1.1 (LPO) en
 * tanto cuanto forme parte �ntegra del total del producto denominado:     openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *     Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *     Partes del c�digo son CopyRight � 2002-2005 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son CopyRight  � 2003-2005 de Consultor�a y Soporte en Redes y  Tecnologías de 
 * la  Informaci�n S.L., otras partes son adaptadas, ampliadas, traducidas, revisadas y/o mejoradas a partir de c�digo original
 * de terceros, recogidos en el ADDENDUM A, secci�n 3 (A.3) de dicha licencia LPO, y si dicho c�digo
 * es extraido como parte del total del producto, estar� sujeto a sus respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 ************************************************************************************************************************************************/
package org.openXpertya.model;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.openXpertya.model.CacheMetadata.ResultColumnMetadata;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.common.PluginPOUtils;
import org.openXpertya.plugin.handlersPO.PluginPOAfterDeleteHandler;
import org.openXpertya.plugin.handlersPO.PluginPOAfterSaveHandler;
import org.openXpertya.plugin.handlersPO.PluginPOBeforeDeleteHandler;
import org.openXpertya.plugin.handlersPO.PluginPOBeforeSaveHandler;
import org.openXpertya.plugin.handlersPO.PluginPOHandler;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocActionStatusEvent;
import org.openXpertya.process.DocActionStatusListener;
import org.openXpertya.util.AuxiliarDTO;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.CacheMgt;
import org.openXpertya.util.DB;
import org.openXpertya.util.DBException;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Evaluatee;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trace;
import org.openXpertya.util.Util;

/**
 * Persistent Object. Superclass for actual implementations
 * 
 * @author Comunidad de Desarrollo openXpertya *Basado en Codigo Original
 *         Modificado, Revisado y Optimizado de: * Jorg Janke
 * @version $Id: PO.java,v 1.107 2005/05/21 04:47:54 jjanke Exp $
 */
public abstract class PO implements Serializable, Comparator, Evaluatee {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final BigDecimal HUNDRED = new BigDecimal(100);
	
	/**
	 * Set Document Value Workflow Manager
	 * 
	 * @param docWFMgr
	 *            mgr
	 */
	public static void setDocWorkflowMgr(DocWorkflowMgr docWFMgr) {
		s_docWFMgr = docWFMgr;
		s_log.config(s_docWFMgr.toString());
	} // setDocWorkflowMgr

	/** Document Value Workflow Manager */
	private static transient DocWorkflowMgr s_docWFMgr = null;

	public static final String SEPARATORUID = "-";

	public static Map<String, Boolean> docs_justPrepared;
	
	static{
		docs_justPrepared = new HashMap<String, Boolean>();
	}
	
	protected boolean existsJustPreparedDoc(){
		return docs_justPrepared.containsKey(get_Table_ID() + "_" + getID());
	}
	
	private class QueryParam {
		private String columnName;
		private Object value;
		private Class valueClass;
		private int columnIndex;
		private int columnDataType;

		public QueryParam(String columnName, Object value, Class c,
				int columnIndex, int columnDataType) {
			this.columnName = columnName;
			this.value = value;
			this.valueClass = c;
			this.columnIndex = columnIndex;
			this.columnDataType = columnDataType;
		}

		public String getColumnName() {
			return columnName;
		}

		public Object getValue() {
			return value;
		}

		public Class getValueClass() {
			return valueClass;
		}

		public int getColumnIndex() {
			return columnIndex;
		}

		public int getColumnDataType() {
			return columnDataType;
		}
	}

	/**************************************************************************
	 * Create New Persisent Object
	 * 
	 * @param ctx
	 *            context
	 */
	public PO(Properties ctx) {
		this(ctx, 0, null, null);
	} // PO

	/**
	 * Create & Load existing Persistent Object
	 * 
	 * @para ID The unique ID of the object
	 * @param ctx
	 *            context
	 * @param ID
	 *            Record_ID or 0 for new
	 * @param trxName
	 *            transaction name
	 */
	public PO(Properties ctx, int ID, String trxName) {
		this(ctx, ID, trxName, null);
	} // PO

	/**
	 * Create & Load existing Persistent Object.
	 * 
	 * @param ctx
	 *            context
	 * @param rs
	 *            optional - load from current result set position (no
	 *            navigation, not closed) if null, a new record is created.
	 * @param trxName
	 *            transaction name
	 */
	public PO(Properties ctx, ResultSet rs, String trxName) {
		this(ctx, 0, trxName, rs);
	} // PO

	/**
	 * Create & Load existing Persistent Object.
	 * 
	 * <pre>
	 *  You load
	 * 		- an existing single key record with 	new PO (ctx, Record_ID)
	 * 			or									new PO (ctx, Record_ID, trxName)
	 * 			or									new PO (ctx, rs, get_TrxName())
	 * 		- a new single key record with			new PO (ctx, 0)
	 * 		- an existing multi key record with		new PO (ctx, rs, get_TrxName())
	 * 		- a new multi key record with			new PO (ctx, null)
	 *  The ID for new single key records is created automatically,
	 *  you need to set the IDs for multi-key records explicitly.
	 * </pre>
	 * 
	 * @param ctx
	 *            context
	 * @param ID
	 *            the ID if 0, the record defaults are applied - ignored if re
	 *            exists
	 * @param trxName
	 *            transaction name
	 * @param rs
	 *            optional - load from current result set position (no
	 *            navigation, not closed)
	 */
	public PO(Properties ctx, int ID, String trxName, ResultSet rs) {
		if (ctx == null)
			throw new IllegalArgumentException("No Context");
		p_ctx = ctx;
		m_trxName = trxName;
		initialize(ID, rs);
	} // PO

	protected void initialize(int ID, ResultSet rs) {
		p_info = initPO(p_ctx);
		if (p_info == null || p_info.getTableName() == null)
			throw new IllegalArgumentException("Invalid PO Info - " + p_info);
		//
		int size = p_info.getColumnCount();
		m_oldValues = new Object[size];
		m_newValues = new Object[size];
		if (rs != null)
			load(rs);
		else
			load(ID, m_trxName);
	}

	/**
	 * Create New PO by Copying existing (key not copied).
	 * 
	 * @param ctx
	 *            context
	 * @param source
	 *            souce object
	 * @param AD_Client_ID
	 *            client
	 * @param AD_Org_ID
	 *            org
	 */
	public PO(Properties ctx, PO source, int AD_Client_ID, int AD_Org_ID) {
		this(ctx, 0, null, null); // create new
		//
		if (source != null)
			copyValues(source, this);
		setAD_Client_ID(AD_Client_ID);
		setAD_Org_ID(AD_Org_ID);
	} // PO

	/** Logger */
	protected transient CLogger log = CLogger.getCLogger(getClass());
	/** Static Logger */
	private static transient CLogger s_log = CLogger.getCLogger(PO.class);

	/** Context */
	protected Properties p_ctx;
	/** Model Info */
	protected volatile POInfo p_info = null;

	/** Original Values */
	private Object[] m_oldValues = null;
	/** New Valies */
	private Object[] m_newValues = null;

	/** Record_IDs */
	private Object[] m_IDs = new Object[] { I_ZERO };
	/** Key Columns */
	private String[] m_KeyColumns = null;
	/** Create New for Multi Key */
	private boolean m_createNew = false;
	/** Attachment with entriess */
	private MAttachment m_attachment = null;
	/** Deleted ID */
	private int m_idOld = 0;
	/** Custom Columns */
	private HashMap m_custom = null;

	/** NULL value */
	private static final Object NULL = new Object();
	/** Zero Integer */
	private static final Integer I_ZERO = new Integer(0);
	/** Accounting Columns */
	private ArrayList s_acctColumns = null;
	/** DocAction Status Listeners */
	private transient List<DocActionStatusListener> m_docActionStatusListeners = new ArrayList<DocActionStatusListener>();

	/** Generalizacion de variable para documentos */
	protected String m_processMsg = "";
	protected String summary = "";
	
	private boolean isFromTab = false;
	
	/**
	 * Initialize and return PO_Info
	 * 
	 * @param ctx
	 *            context
	 * @return POInfo
	 */
	abstract protected POInfo initPO(Properties ctx);

	/**
	 * String representation
	 * 
	 * @return String representation
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("PO[").append(get_WhereClause(true))
				.append("]");
		return sb.toString();
	} // toString

	/**
	 * Equals based on ID
	 * 
	 * @param cmp
	 *            comperator
	 * @return true if ID the same
	 */
	public boolean equals(Object cmp) {
		if (cmp == null)
			return false;
		if (!(cmp instanceof PO))
			return false;
		if (cmp.getClass().equals(this.getClass()))
			return ((PO) cmp).getID() == getID();
		return super.equals(cmp);
	} // equals

	/**
	 * Compare based on DocumentNo, Value, Name, Description
	 * 
	 * @param o1
	 *            Object 1
	 * @param o2
	 *            Object 2
	 * @return -1 if o1 < o2
	 */
	public int compare(Object o1, Object o2) {
		if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		if (!(o1 instanceof PO))
			throw new ClassCastException("Not PO -1- " + o1);
		if (!(o2 instanceof PO))
			throw new ClassCastException("Not PO -2- " + o2);
		// same class
		if (o1.getClass().equals(o2.getClass())) {
			int index = get_ColumnIndex("DocumentNo");
			if (index == -1)
				index = get_ColumnIndex("Value");
			if (index == -1)
				index = get_ColumnIndex("Name");
			if (index == -1)
				index = get_ColumnIndex("Description");
			if (index != -1) {
				PO po1 = (PO) o1;
				Object comp1 = po1.get_Value(index);
				PO po2 = (PO) o2;
				Object comp2 = po2.get_Value(index);
				if (comp1 == null)
					return -1;
				else if (comp2 == null)
					return 1;
				return comp1.toString().compareTo(comp2.toString());
			}
		}
		return o1.toString().compareTo(o2.toString());
	} // compare

	/**
	 * Get TableName.
	 * 
	 * @return table name
	 */
	public String get_TableName() {
		return p_info.getTableName();
	} // getTableName

	/**
	 * Return Single Key Record ID
	 * 
	 * @return ID or 0
	 */
	public int getID() {
		Object oo = m_IDs[0];
		if (oo != null && oo instanceof Integer)
			return ((Integer) oo).intValue();
		return 0;
	} // getID

	/**
	 * Return Deleted Single Key Record ID
	 * 
	 * @return ID or 0
	 */
	public int getIDOld() {
		return m_idOld;
	} // getID

	/**
	 * Get Context
	 * 
	 * @return context
	 */
	public Properties getCtx() {
		return p_ctx;
	} // getCtx

	/**************************************************************************
	 * Get Value
	 * 
	 * @param index
	 *            index
	 * @return value
	 */
	public final Object get_Value(int index) {
		if (index < 0 || index >= get_ColumnCount()) {
			log.log(Level.SEVERE, "Index invalid - " + index);
			return null;
		}
		if (m_newValues[index] != null) {

			if (m_newValues[index].equals(NULL)) {
				return null;
			}
			return m_newValues[index];
		}
		return m_oldValues[index];
	} // get_Value

	/**
	 * Get Value as int
	 * 
	 * @param index
	 *            index
	 * @return int value or 0
	 */
	protected int get_ValueAsInt(int index) {
		Object value = get_Value(index);
		if (value == null)
			return 0;
		if (value instanceof Integer)
			return ((Integer) value).intValue();
		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException ex) {
			log.warning(p_info.getColumnName(index) + " - " + ex.getMessage());
			return 0;
		}
	} // get_ValueAsInt

	/**
	 * Get Value
	 * 
	 * @param columnName
	 *            column name
	 * @return value or null
	 */
	public final Object get_Value(String columnName) {
		int index = get_ColumnIndex(columnName);
		if (index < 0) {
			log.log(Level.SEVERE, "Column not found - " + columnName);
			Trace.printStack();
			log.log(Level.SEVERE, "En Po.Get_value, retornado un nulo");
			return null;
		}
		return get_Value(index);
	} // get_Value

	/**
	 * Get Encrypted Value
	 * 
	 * @param columnName
	 *            column name
	 * @return value or null
	 */
	protected final Object get_ValueE(String columnName) {
		return get_Value(columnName);
	} // get_ValueE

	/**
	 * Get Column Value
	 * 
	 * @param variableName
	 *            name
	 * @return value or ""
	 */
	public String get_ValueAsString(String variableName) {
		Object value = get_Value(variableName);
		if (value == null)
			return "";
		return value.toString();
	} // get_ValueAsString

	/**
	 * Get Value of Column
	 * 
	 * @param AD_Column_ID
	 *            column
	 * @return value or null
	 */
	public final Object get_ValueOfColumn(int AD_Column_ID) {
		int index = p_info.getColumnIndex(AD_Column_ID);
		if (index < 0) {
			log.log(Level.SEVERE, "Not found - AD_Column_ID=" + AD_Column_ID);
			return null;
		}
		return get_Value(index);
	} // get_ValueOfColumn

	/**
	 * Get Old Value
	 * 
	 * @param index
	 *            index
	 * @return value
	 */
	public final Object get_ValueOld(int index) {
		if (index < 0 || index >= get_ColumnCount()) {
			log.log(Level.SEVERE, "Index invalid - " + index);
			return null;
		}
		return m_oldValues[index];
	} // get_ValueOld

	/**
	 * Get Old Value
	 * 
	 * @param columnName
	 *            column name
	 * @return value or null
	 */
	public final Object get_ValueOld(String columnName) {
		int index = get_ColumnIndex(columnName);
		if (index < 0) {
			log.log(Level.SEVERE, "Column not found - " + columnName);
			return null;
		}
		return get_ValueOld(index);
	} // get_ValueOld

	/**
	 * Get Old Value as int
	 * 
	 * @param columnName
	 *            column name
	 * @return int value or 0
	 */
	protected int get_ValueOldAsInt(String columnName) {
		Object value = get_ValueOld(columnName);
		if (value == null)
			return 0;
		if (value instanceof Integer)
			return ((Integer) value).intValue();
		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException ex) {
			log.warning(columnName + " - " + ex.getMessage());
			return 0;
		}
	} // get_ValueOldAsInt

	/**
	 * Is Value Changed
	 * 
	 * @param index
	 *            index
	 * @return true if changed
	 */
	public final boolean is_ValueChanged(int index) {
		if (index < 0 || index >= get_ColumnCount()) {
			log.log(Level.SEVERE, "Index invalid - " + index);
			return false;
		}
		if (m_newValues[index] == null)
			return false;
		return !m_newValues[index].equals(m_oldValues[index]);
	} // is_ValueChanged

	/**
	 * Is Value Changed
	 * 
	 * @param columnName
	 *            column name
	 * @return true if changed
	 */
	public final boolean is_ValueChanged(String columnName) {
		int index = get_ColumnIndex(columnName);
		if (index < 0) {
			log.log(Level.SEVERE, "Column not found - " + columnName);
			return false;
		}
		return is_ValueChanged(index);
	} // is_ValueChanged

	/**
	 * Return new - old. - New Value if Old Valus is null - New Value - Old
	 * Value if Number - otherwise null
	 * 
	 * @param index
	 *            index
	 * @return new - old or null if not appropiate or not changed
	 */
	public final Object get_ValueDifference(int index) {
		if (index < 0 || index >= get_ColumnCount()) {
			log.log(Level.SEVERE, "Index invalid - " + index);
			return null;
		}
		Object nValue = m_newValues[index];
		// No new Value or NULL
		if (nValue == null || nValue == NULL)
			return null;
		//
		Object oValue = m_oldValues[index];
		if (oValue == null || oValue == NULL)
			return nValue;
		if (nValue instanceof BigDecimal) {
			BigDecimal obd = (BigDecimal) oValue;
			return ((BigDecimal) nValue).subtract(obd);
		} else if (nValue instanceof Integer) {
			int result = ((Integer) nValue).intValue();
			result -= ((Integer) oValue).intValue();
			return new Integer(result);
		}
		//
		log.warning("Invalid type - New=" + nValue);
		return null;
	} // get_ValueDifference

	/**
	 * Return new - old. - New Value if Old Valus is null - New Value - Old
	 * Value if Number - otherwise null
	 * 
	 * @param columnName
	 *            column name
	 * @return new - old or null if not appropiate or not changed
	 */
	public final Object get_ValueDifference(String columnName) {
		int index = get_ColumnIndex(columnName);
		if (index < 0) {
			log.log(Level.SEVERE, "Column not found - " + columnName);
			return null;
		}
		return get_ValueDifference(index);
	} // get_ValueDifference

	/**************************************************************************
	 * Set Value
	 * 
	 * @param ColumnName
	 *            column name
	 * @param value
	 *            value
	 * @return true if value set
	 */
	public final boolean set_Value(String ColumnName, Object value) {
		int index = get_ColumnIndex(ColumnName);

		if (index < 0) {
			log.log(Level.SEVERE, "Column not found - " + ColumnName);
			return false;
		}
		return set_Value(index, value);
	} // setValue

	/**
	 * Set Encrypted Value
	 * 
	 * @param ColumnName
	 *            column name
	 * @param value
	 *            value
	 * @return true if value set
	 */
	protected final boolean set_ValueE(String ColumnName, Object value) {
		return set_Value(ColumnName, value);
	} // setValueE

	/**
	 * Set Value if updateable and correct class. (and to NULL if not mandatory)
	 * 
	 * @param index
	 *            index
	 * @param value
	 *            value
	 * @return true if value set
	 */
	protected final boolean set_Value(int index, Object value) {
		getLog().fine("Iniciando Set_Value con index = " + index);
		if (index < 0 || index >= get_ColumnCount()) {
			getLog().log(Level.SEVERE, "Index invalid - " + index);
			return false;
		}
		getLog().fine("Iniciando Set_Value con index = " + index);
		String prueba = p_info.getTableName();
		getLog().fine("La tableName es = " + prueba);
		int nuncol = p_info.getColumnCount();
		getLog().fine(" Y tiene  = " + nuncol + " columnas");
		getLog().fine("Y las columnas son= ");
		for (int i = 0; i < nuncol; i++) {
			getLog().fine("El campo es = " + p_info.getColumnName(i));
		}

		String ColumnName = p_info.getColumnName(index);
		String colInfo = " - " + ColumnName;
		//
		if (p_info.isVirtualColumn(index)) {
			getLog().log(Level.SEVERE, "Virtual Column" + colInfo);
			return false;
		}
		//
		if (!p_info.isColumnUpdateable(index)) {
			colInfo += " - NewValue=" + value + " - OldValue="
					+ get_Value(index);
			getLog().log(Level.SEVERE, "Column not updateable" + colInfo);
			return false;
		}
		//

		if (value == null) {
			if (p_info.isColumnMandatory(index)) {
				getLog().log(Level.SEVERE, "Cannot set mandatory column to null "
						+ colInfo);
				// Trace.printStack();
				return false;
			}
			m_newValues[index] = NULL; // correct
			getLog().finer(ColumnName + " => null");
		} else {
			getLog().fine("En po.else");
			getLog().fine("En po.else con value.getClass() =" + value.getClass());
			getLog().fine("En po.else con p_info.getColumnClass(index) ="
					+ p_info.getColumnClass(index));

			// matching class or generic object
			if (value.getClass().equals(p_info.getColumnClass(index))
					|| p_info.getColumnClass(index) == Object.class) {
				getLog().fine("En el primer if con value= " + value);
				m_newValues[index] = value;
			} // correct
			// Integer can be set as BigDecimal
			else if (value.getClass() == BigDecimal.class
					&& p_info.getColumnClass(index) == Integer.class) {
				getLog().fine("En el segundo if con value= " + value);
				m_newValues[index] = new Integer(((BigDecimal) value)
						.intValue());
			}
			// Set Boolean
			else if (p_info.getColumnClass(index) == Boolean.class
					&& ("Y".equals(value) || "N".equals(value))) {
				getLog().fine("En el tercer if con value= " + value);
				m_newValues[index] = new Boolean("Y".equals(value));

			}

			else {
				getLog().log(Level.SEVERE, ColumnName + " - Class invalid: "
						+ value.getClass().toString() + ", Should be "
						+ p_info.getColumnClass(index).toString() + ": "
						+ value);
				return false;
			}
			// Validate (Min/Max)
			String error = p_info.validate(index, value);
			if (error != null) {
				getLog().log(Level.SEVERE, ColumnName + "=" + value + " - " + error);
				return false;
			}
			// Length for String
			if (p_info.getColumnClass(index) == String.class) {
				String stringValue = value.toString();
				int length = p_info.getFieldLength(index);
				if (stringValue.length() > length) {
					getLog().warning(ColumnName
							+ " - Value too long - truncated to length="
							+ length);
					m_newValues[index] = stringValue.substring(0, length - 1);
				}
			}
			getLog().finest(ColumnName + " ==> " + m_newValues[index]);
		}
		setKeys(ColumnName, m_newValues[index]);
		return true;
	} // setValue

	/**
	 * Set Value w/o check (update, r/o, ..). Used when Column is R/O Required
	 * for key and parent values
	 * 
	 * @param ColumnName
	 *            column name
	 * @param value
	 *            value
	 * @return true if value set
	 */
	public final boolean set_ValueNoCheck(String ColumnName, Object value) {
		int index = get_ColumnIndex(ColumnName);
		if (index < 0) {
			log.log(Level.SEVERE, "Column not found - " + ColumnName);
			return false;
		}
		if (value == null)
			m_newValues[index] = NULL; // write direct
		else {
			// matching class or generic object
			if (value.getClass().equals(p_info.getColumnClass(index))
					|| p_info.getColumnClass(index) == Object.class)
				m_newValues[index] = value; // correct
			// Integer can be set as BigDecimal
			else if (value.getClass() == BigDecimal.class
					&& p_info.getColumnClass(index) == Integer.class)
				m_newValues[index] = new Integer(((BigDecimal) value)
						.intValue());
			// Set Boolean
			else if (p_info.getColumnClass(index) == Boolean.class
					&& ("Y".equals(value) || "N".equals(value)))
				m_newValues[index] = new Boolean("Y".equals(value));
			else {
				log.warning(ColumnName + " - Class invalid: "
						+ value.getClass().toString() + ", Should be "
						+ p_info.getColumnClass(index).toString() + ": "
						+ value);
				m_newValues[index] = value; // correct
			}
			// Validate (Min/Max)
			String error = p_info.validate(index, value);
			if (error != null)
				log.warning(ColumnName + "=" + value + " - " + error);
			// length for String
			if (p_info.getColumnClass(index) == String.class) {
				String stringValue = value.toString();
				int length = p_info.getFieldLength(index);
				if (stringValue.length() > length) {
					log.warning(ColumnName
							+ " - Value too long - truncated to length="
							+ length);
					m_newValues[index] = stringValue.substring(0, length - 1);
				}
			}
		}
		log.finest(ColumnName
				+ " = "
				+ m_newValues[index]
				+ " ("
				+ (m_newValues[index] == null ? "-" : m_newValues[index]
						.getClass().getName()) + ")");
		setKeys(ColumnName, m_newValues[index]);
		return true;
	} // set_ValueNoCheck

	/**
	 * Set Encrypted Value w/o check (update, r/o, ..). Used when Column is R/O
	 * Required for key and parent values
	 * 
	 * @param ColumnName
	 *            column name
	 * @param value
	 *            value
	 * @return true if value set
	 */
	protected final boolean set_ValueNoCheckE(String ColumnName, Object value) {
		return set_ValueNoCheckE(ColumnName, value);
	} // set_ValueNoCheckE

	/**
	 * Set Value of Column
	 * 
	 * @param AD_Column_ID
	 *            column
	 * @param value
	 *            value
	 */
	public final void set_ValueOfColumn(int AD_Column_ID, Object value) {
		int index = p_info.getColumnIndex(AD_Column_ID);
		if (index < 0)
			log.log(Level.SEVERE, "Not found - AD_Column_ID=" + AD_Column_ID);
		set_Value(index, value);
	} // setValueOfColumn

	/**
	 * Set Value of Column with value of parameter columnName
	 * 
	 * @param columnName
	 *            column name
	 * @param value
	 *            value
	 */
	public final void set_ValueOfColumn(String columnName, Object value) {
		int index = p_info.getColumnIndexIgnoreCase(columnName);
		if (index < 0)
			log.log(Level.SEVERE, "Not found - AD_Column_ID=" + columnName);
		set_Value(index, value);
	} // setValueOfColumn

	/**
	 * Set Custom Column
	 * 
	 * @param columnName
	 *            column
	 * @param value
	 *            value
	 */
	public final void set_CustomColumn(String columnName, Object value) {
		if (m_custom == null)
			m_custom = new HashMap();

		String valueString = "NULL";
		if (value == null)
			;
		else if (value instanceof Number)
			valueString = value.toString();
		else if (value instanceof Boolean)
			valueString = ((Boolean) value).booleanValue() ? "'Y'" : "'N'";
		else if (value instanceof Timestamp)
			valueString = DB.TO_DATE((Timestamp) value, false);
		else
			// if (value instanceof String)
			valueString = DB.TO_STRING(value.toString());
		// Save it

		log.log(Level.SEVERE, columnName + "=" + valueString);
		m_custom.put(columnName, valueString);

	} // set_CustomColumn

	/**
	 * Set (numeric) Key Value
	 * 
	 * @param ColumnName
	 *            column name
	 * @param value
	 *            value
	 */
	private void setKeys(String ColumnName, Object value) {
		// Update if KeyColumn
		for (int i = 0; i < m_IDs.length; i++) {
			if (ColumnName.equals(m_KeyColumns[i])) {
				m_IDs[i] = value;
			}
		} // for all key columns
	} // setKeys

	/**************************************************************************
	 * Get Column Count
	 * 
	 * @return column count
	 */
	protected int get_ColumnCount() {
		return p_info.getColumnCount();
	} // getColumnCount

	/**
	 * Get Column Name
	 * 
	 * @param index
	 *            index
	 * @return ColumnName
	 */
	protected String get_ColumnName(int index) {
		return p_info.getColumnName(index);
	} // getColumnName

	/**
	 * Get Column Label
	 * 
	 * @param index
	 *            index
	 * @return Column Label
	 */
	protected String get_ColumnLabel(int index) {
		return p_info.getColumnLabel(index);
	} // getColumnLabel

	/**
	 * Get Column Description
	 * 
	 * @param index
	 *            index
	 * @return column description
	 */
	protected String get_ColumnDescription(int index) {
		return p_info.getColumnDescription(index);
	} // getColumnDescription

	/**
	 * Is Column Mandatory
	 * 
	 * @param index
	 *            index
	 * @return true if column mandatory
	 */
	protected boolean isColumnMandatory(int index) {
		return p_info.isColumnMandatory(index);
	} // isColumnNandatory

	/**
	 * Is Column Updateable
	 * 
	 * @param index
	 *            index
	 * @return true if column updateable
	 */
	protected boolean isColumnUpdateable(int index) {
		return p_info.isColumnUpdateable(index);
	} // isColumnUpdateable

	/**
	 * Set Column Updateable
	 * 
	 * @param index
	 *            index
	 * @param updateable
	 *            column updateable
	 */
	protected void set_ColumnUpdateable(int index, boolean updateable) {
		p_info.setColumnUpdateable(index, updateable);
	} // setColumnUpdateable

	/**
	 * Set all columns updateable
	 * 
	 * @param updateable
	 *            updateable
	 */
	protected void setUpdateable(boolean updateable) {
		p_info.setUpdateable(updateable);
	} // setUpdateable

	/**
	 * Get Column DisplayType
	 * 
	 * @param index
	 *            index
	 */
	protected int get_ColumnDisplayType(int index) {
		return p_info.getColumnDisplayType(index);
	} // getColumnDisplayType

	/**
	 * Get Lookup
	 * 
	 * @param index
	 *            index
	 * @return Lookup or null
	 */
	protected Lookup get_ColumnLookup(int index) {
		return p_info.getColumnLookup(index);
	} // getColumnLookup

	/**
	 * Get Column Index
	 * 
	 * @param columnName
	 *            column name
	 * @return index of column with ColumnName or -1 if not found
	 */
	public final int get_ColumnIndex(String columnName) {

		return p_info.getColumnIndex(columnName);
	} // getColumnIndex

	/**
	 * Get Column Index
	 * 
	 * @param columnName
	 *            column name
	 * @return index of column with ColumnName or -1 if not found
	 */
	public final int get_ColumnIndexIgnoreCase(String columnName) {
		return p_info.getColumnIndexIgnoreCase(columnName);
	} // getColumnIndex

	/**
	 * Get Display Value of value
	 * 
	 * @param columnName
	 *            columnName
	 * @param currentValue
	 *            current value
	 * @return String value with "./." as null
	 */
	public String get_DisplayValue(String columnName, boolean currentValue) {
		Object value = currentValue ? get_Value(columnName)
				: get_ValueOld(columnName);
		if (value == null)
			return "./.";
		String retValue = value.toString();
		int index = get_ColumnIndex(columnName);
		if (index < 0)
			return retValue;
		int dt = get_ColumnDisplayType(index);
		if (DisplayType.isText(dt) || DisplayType.YesNo == dt)
			return retValue;
		// Lookup
		Lookup lookup = get_ColumnLookup(index);
		if (lookup != null)
			return lookup.getDisplay(value);
		// Other
		return retValue;
	} // get_DisplayValue

	/**
	 * Copy old values of From to new values of To. Does not copy Keys
	 * 
	 * @param from
	 *            old, existing & unchanged PO
	 * @param to
	 *            new, not saved PO
	 * @param AD_Client_ID
	 *            client
	 * @param AD_Org_ID
	 *            org
	 */
	protected static void copyValues(PO from, PO to, int AD_Client_ID,
			int AD_Org_ID) {
		copyValues(from, to);
		to.setAD_Client_ID(AD_Client_ID);
		to.setAD_Org_ID(AD_Org_ID);
	} // copyValues

	/**
	 * Copy old values of From to new values of To. Does not copy Keys and
	 * AD_Client_ID/AD_Org_ID
	 * 
	 * @param from
	 *            old, existing & unchanged PO
	 * @param to
	 *            new, not saved PO
	 */
	public static void copyValues(PO from, PO to) {
		copyValues(from, to, null);
	} // copy

	/**
	 * Copia valores de las columnas de un PO a otro. Las columnas excuyentes
	 * parámetro no se copian.
	 * 
	 * @param from
	 *            PO from
	 * @param to
	 *            PO to
	 * @param excludedColumnsName
	 *            nombre de las columnas que se deben excluir de copia
	 */
	public static void copyValues(PO from, PO to, List<String> excludedColumnsName) {
		s_log.fine("From ID=" + from.getID() + " - To ID=" + to.getID());
		if (from.getClass() != to.getClass())
			throw new IllegalArgumentException("To class=" + to.getClass()
					+ " NOT From=" + from.getClass());
		//
		for (int i = 0; i < from.m_oldValues.length; i++) {
			if (from.p_info.isVirtualColumn(i))
				continue;
			String colName = from.p_info.getColumnName(i);
			// Ignore Standard Values
			if (colName.startsWith("Created")
					|| colName.startsWith("Updated")
					// || colName.equals(from.getTableName() + "_ID") //
					// KeyColumn
					|| from.p_info.isColumnKey(i) 
					|| colName.equals("IsActive")
					|| colName.equals("AD_Client_ID")
					|| colName.equals("AD_Org_ID")
					|| colName.equals("AD_ComponentVersion_ID")
					|| colName.equals("AD_ComponentObjectUID")
					|| colName.equals("Posted")
					|| (excludedColumnsName != null && excludedColumnsName
							.contains(colName)))
				; // ignore
			else {
				int toIdx = to.p_info.getColumnIndex(colName);

				if (toIdx == -1)
					s_log.severe("copyValues: column " + colName
							+ " not found!");

				if (toIdx != i)
					s_log.log(Level.INFO, "copyValues: toIdx != fromIndex");

				to.m_newValues[toIdx] = from.m_oldValues[i];
			}
		}
	} // copy

	
	/**
	 * Make a complete copy of old and new values of From to old and new values
	 * of To.
	 */
	public static void deepCopyValues(PO from, PO to) {
		s_log.fine("From ID=" + from.getID() + " - To ID=" + to.getID());
		for (int i = 0; i < from.m_oldValues.length; i++) {
			if (from.p_info.isVirtualColumn(i))
				continue;
			String colName = from.p_info.getColumnName(i);
			int toIdx = to.p_info.getColumnIndex(colName);

			if (toIdx == -1) {
				s_log.severe("copyValues: column " + colName + " not found!");
				continue;
			}

			to.m_newValues[toIdx] = from.m_newValues[i];
			to.m_oldValues[toIdx] = from.m_oldValues[i];
		}
		from.copyInstanceValues(to);
	} // copy

	protected static boolean ignoreColumnOnCopyValues(String colName, PO p,
			int colIdx) {
		// Ignore Standard Values

		return (colName.startsWith("Created") || colName.startsWith("Updated")
				// || colName.equals(from.getTableName() + "_ID") // KeyColumn
				|| p.p_info.isColumnKey(colIdx) || colName.equals("IsActive")
				|| colName.equals("AD_Client_ID") || colName
				.equals("AD_Org_ID"));
	}

	protected static void copyValuesEvaluatee(Evaluatee from, PO to) {
		for (int i = 0; i < to.get_ColumnCount(); i++) {
			if (to.p_info.isVirtualColumn(i))
				continue;

			String colName = to.get_ColumnName(i);

			if (!ignoreColumnOnCopyValues(colName, to, i)) {
				to.set_Value(i, from.get_ValueAsString(colName));
			}
		}
	} // copy

	/**************************************************************************
	 * Load record with ID
	 * 
	 * @param ID
	 *            ID
	 * @param trxName
	 *            transaction name
	 */
	protected void load(int ID, String trxName) {
		log.finest("Po.load  ID=" + ID);
		if (ID > 0) {
			m_IDs = new Object[] { new Integer(ID) };
			m_KeyColumns = new String[] { p_info.getTableName() + "_ID" };
			load(trxName);
		} else // new
		{
			loadDefaults();
			m_createNew = true;
			setKeyInfo(); // sets m_IDs
			loadComplete(true);
		}
	} // load

	/**
	 * (re)Load record with m_ID[*]
	 */
	public boolean load(String trxName) {
		m_trxName = trxName;
		log.finest("Po.load  m_trxName=" + trxName);
		boolean success = true;
		StringBuffer sql = new StringBuffer("SELECT ");
		int size = get_ColumnCount();
		for (int i = 0; i < size; i++) {
			if (i != 0)
				sql.append(",");
			sql.append(p_info.getColumnSQL(i)); // Normal and Virtual Column
		}
		sql.append(" FROM ").append(p_info.getTableName()).append(" WHERE ")
				.append(get_WhereClause(false));

		//
		// int index = -1;
		log.finest("get_whereClause(true) =");
		log.finest(get_WhereClause(true));
		if (CLogMgt.isLevelFinest())
			log.finest(get_WhereClause(true));
		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(sql.toString(), m_trxName); // local trx
			// only
			for (int i = 0; i < m_IDs.length; i++) {
				Object oo = m_IDs[i];
				if (oo instanceof Integer)
					pstmt.setInt(i + 1, ((Integer) m_IDs[i]).intValue());
				else
					pstmt.setString(i + 1, m_IDs[i].toString());
			}
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				success = load(rs);
				/**
				 * load column values for (index = 0; index < size; index++) {
				 * Class clazz = p_info.getColumnClass(index); int dt =
				 * p_info.getColumnDisplayType(index); // if (clazz ==
				 * Integer.class) m_oldValues[index] = new
				 * Integer(rs.getInt(index+1)); else if (clazz ==
				 * BigDecimal.class) m_oldValues[index] =
				 * rs.getBigDecimal(index+1); else if (clazz == Boolean.class)
				 * m_oldValues[index] = new Boolean
				 * ("Y".equals(rs.getString(index+1))); else if (clazz ==
				 * Timestamp.class) m_oldValues[index] =
				 * rs.getTimestamp(index+1); else if (DisplayType.isLOB(dt))
				 * m_oldValues[index] = get_LOB (rs.getObject(index+1)); else if
				 * (clazz == String.class) m_oldValues[index] =
				 * rs.getString(index+1); else m_oldValues[index] =
				 * loadSpecial(rs, index); if (rs.wasNull() &&
				 * m_oldValues[index] != null) m_oldValues[index] = null; // if
				 * (CLogMgt.isLevelFinest()) log.log(Level.ALL,
				 * String.valueOf(index) + ": " + p_info.getColumnName(index) +
				 * "(" + p_info.getColumnClass(index) + ") = " +
				 * m_oldValues[index]); }
				 */
			} else {
				log.log(Level.SEVERE, "NO Data found for "
						+ get_WhereClause(true), new Exception());
				m_IDs = new Object[] { I_ZERO };
				success = false;
				// throw new DBException("NO Data found for " +
				// get_WhereClause(true));
			}
			rs.close();
			pstmt.close();
			pstmt = null;
			m_createNew = false;
			// reset new values
			m_newValues = new Object[size];
		} catch (Exception e) {
			success = false;
			m_IDs = new Object[] { I_ZERO };
			String msg = "";
			if (m_trxName != null)
				msg = "[" + m_trxName + "] - ";
			msg += get_WhereClause(true)
			// + ", Index=" + index
					// + ", Column=" + get_ColumnName(index)
					// + ", " + p_info.toString(index)
					+ ", SQL=" + sql.toString();
			log.log(Level.SEVERE, msg, e);
			// throw new DBException(e);
		}
		// Finish
		try {
			if (pstmt != null)
				pstmt.close();
			pstmt = null;
		} catch (SQLException e1) {
		}
		loadComplete(success);
		return success;
	} // load

	/**
	 * Load from the current position of a ResultSet
	 * 
	 * @param rs
	 *            result set
	 */
	protected boolean load(ResultSet rs) {
		int size = get_ColumnCount();
		boolean success = true;
		int index = 0;
		// load column values
		for (index = 0; index < size; index++) {
			String columnName = p_info.getColumnName(index);
			Class clazz = p_info.getColumnClass(index);
			int dt = p_info.getColumnDisplayType(index);
			// Continue if is virtual column.
			if (p_info.isVirtualColumn(index))
				// FIXME: se debería ejecutar la consulta SQL de la columna y
				// asignar
				// el valor a la columna.
				continue;

			try {
				if (clazz == Integer.class)
					m_oldValues[index] = new Integer(rs.getInt(columnName));
				else if (clazz == BigDecimal.class)
					m_oldValues[index] = rs.getBigDecimal(columnName);
				else if (clazz == Boolean.class)
					m_oldValues[index] = new Boolean("Y".equals(rs
							.getString(columnName)));
				else if (clazz == Timestamp.class)
					m_oldValues[index] = rs.getTimestamp(columnName);
				else if (DisplayType.isLOB(dt))
					m_oldValues[index] = get_LOB(rs.getObject(columnName));
				else if (clazz == String.class)
					m_oldValues[index] = rs.getString(columnName);
				else
					m_oldValues[index] = loadSpecial(rs, index);
				// NULL
				if (rs.wasNull() && m_oldValues[index] != null)
					m_oldValues[index] = null;
				//
				if (CLogMgt.isLevelAll())
					log.finest(String.valueOf(index) + ": "
							+ p_info.getColumnName(index) + "("
							+ p_info.getColumnClass(index) + ") = "
							+ m_oldValues[index]);
			} catch (SQLException e) {
				log.log(Level.SEVERE, "(rs) - " + String.valueOf(index) + ": "
						+ p_info.getColumnName(index) + " ("
						+ p_info.getColumnClass(index) + ")", e);
				success = false;
				// throw new DBException(e);
			}
		}
		m_createNew = false;
		setKeyInfo();
		loadComplete(success);
		return success;
	} // load

	/**
	 * Load Special data (images, ..). To be extended by sub-classes
	 * 
	 * @param rs
	 *            result set
	 * @param index
	 *            zero based index
	 * @return value value
	 * @throws SQLException
	 */
	protected Object loadSpecial(ResultSet rs, int index) throws SQLException {
		log.finest("(NOP) - " + p_info.getColumnName(index));
		return null;
	} // loadSpecial

	/**
	 * Load is complete
	 * 
	 * @param success
	 *            success To be extended by sub-classes
	 */
	protected void loadComplete(boolean success) {
	} // loadComplete

	/**
	 * Load Defaults
	 */
	protected void loadDefaults() {
		setStandardDefaults();
		//
		/** @todo defaults from Field */
		// MField.getDefault(p_info.getDefaultLogic(i));
	} // loadDefaults

	/**
	 * Set Default values. Client, Org, Created/Updated, *By, IsActive
	 */
	protected void setStandardDefaults() {
		int size = get_ColumnCount();
		for (int i = 0; i < size; i++) {
			if (p_info.isVirtualColumn(i))
				continue;
			String colName = p_info.getColumnName(i);
			// Set Standard Values
			if (colName.endsWith("tedBy"))
				m_newValues[i] = new Integer(Env.getContextAsInt(p_ctx,
						"#AD_User_ID"));
			else if (colName.equals("Created") || colName.equals("Updated"))
				m_newValues[i] = Env.getTimestamp();
			else if (colName.equals(p_info.getTableName() + "_ID")) // KeyColumn
				m_newValues[i] = I_ZERO;
			else if (colName.equals("IsActive"))
				m_newValues[i] = new Boolean(true);
			else if (colName.equals("AD_Client_ID"))
				m_newValues[i] = new Integer(Env.getAD_Client_ID(p_ctx));
			else if (colName.equals("AD_Org_ID"))
				m_newValues[i] = new Integer(Env.getAD_Org_ID(p_ctx));
			else if (colName.equals("Processed"))
				m_newValues[i] = new Boolean(false);
			else if (colName.equals("Processing"))
				m_newValues[i] = new Boolean(false);
			else if (colName.equals("Posted"))
				m_newValues[i] = new Boolean(false);
		}
	} // setDefaults

	/**
	 * Set Key Info (IDs and KeyColumns).
	 */
	private void setKeyInfo() {
		// Search for Primary Key
		for (int i = 0; i < p_info.getColumnCount(); i++) {
			if (p_info.isColumnKey(i)
					&& p_info.getColumnName(i).endsWith("_ID")) {
				String ColumnName = p_info.getColumnName(i);
				m_KeyColumns = new String[] { ColumnName };
				Integer ii = (Integer) get_Value(i);
				if (ii == null)
					m_IDs = new Object[] { I_ZERO };
				else
					m_IDs = new Object[] { ii };
				log.finest("(PK) " + ColumnName + "=" + ii);
				return;
			}
		} // primary key search

		// Search for Parents
		ArrayList columnNames = new ArrayList();
		for (int i = 0; i < p_info.getColumnCount(); i++) {
			if (p_info.isColumnParent(i))
				columnNames.add(p_info.getColumnName(i));
		}
		// Set FKs
		int size = columnNames.size();
		if (size == 0)
			throw new IllegalStateException("No PK nor FK - "
					+ p_info.getTableName());
		m_IDs = new Object[size];
		m_KeyColumns = new String[size];
		for (int i = 0; i < size; i++) {
			m_KeyColumns[i] = (String) columnNames.get(i);
			if (m_KeyColumns[i].endsWith("_ID")) {
				Integer ii = null;
				try {
					ii = (Integer) get_Value(m_KeyColumns[i]);
				} catch (Exception e) {
					log.log(Level.SEVERE, "", e);
				}
				if (ii != null)
					m_IDs[i] = ii;
			} else
				m_IDs[i] = get_Value(m_KeyColumns[i]);
			log.finest("(FK) " + m_KeyColumns[i] + "=" + m_IDs[i]);
		}
	} // setKeyInfo

	/**************************************************************************
	 * Are all mandatory Fields filled (i.e. can we save)?. Stops at first null
	 * mandatory field
	 * 
	 * @return true if all mandatory fields are ok
	 */
	protected boolean isMandatoryOK() {
		int size = get_ColumnCount();
		for (int i = 0; i < size; i++) {
			if (p_info.isColumnMandatory(i)) {
				if (p_info.isVirtualColumn(i))
					continue;
				if (get_Value(i) == null || get_Value(i).equals(NULL)) {
					log.info(p_info.getColumnName(i));
					return false;
				}
			}
		}
		return true;
	} // isMandatoryOK

	/**************************************************************************
	 * Set AD_Client
	 * 
	 * @param AD_Client_ID
	 *            client
	 */
	final protected void setAD_Client_ID(int AD_Client_ID) {
		set_ValueNoCheck("AD_Client_ID", new Integer(AD_Client_ID));
	} // setAD_Client_ID

	/**
	 * Get AD_Client
	 * 
	 * @return AD_Client_ID
	 */
	public final int getAD_Client_ID() {
		Integer ii = (Integer) get_Value("AD_Client_ID");
		if (ii == null)
			return 0;
		return ii.intValue();
	} // getAD_Client_ID

	/**
	 * Set AD_Org
	 * 
	 * @param AD_Org_ID
	 *            org
	 */
	public void setAD_Org_ID(int AD_Org_ID) {
		set_ValueNoCheck("AD_Org_ID", new Integer(AD_Org_ID));
	} // setAD_Org_ID

	/**
	 * Get AD_Org
	 * 
	 * @return AD_Org_ID
	 */
	public int getAD_Org_ID() {
		Integer ii = (Integer) get_Value("AD_Org_ID");
		if (ii == null)
			return 0;
		return ii.intValue();
	} // getAD_Org_ID

	/**
	 * Overwrite Client Org if different
	 * 
	 * @param AD_Client_ID
	 *            client
	 * @param AD_Org_ID
	 *            org
	 */
	public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
		if (AD_Client_ID != getAD_Client_ID())
			setAD_Client_ID(AD_Client_ID);
		if (AD_Org_ID != getAD_Org_ID())
			setAD_Org_ID(AD_Org_ID);
	} // setClientOrg

	/**
	 * Overwrite Client Org if different
	 * 
	 * @param po
	 *            persistent object
	 */
	protected void setClientOrg(PO po) {
		setClientOrg(po.getAD_Client_ID(), po.getAD_Org_ID());
	} // setClientOrg

	/**
	 * Set Active
	 * 
	 * @param active
	 *            active
	 */
	public final void setIsActive(boolean active) {
		set_Value("IsActive", new Boolean(active));
	} // setActive

	/**
	 * Is Active
	 * 
	 * @return is active
	 */
	public final boolean isActive() {
		Boolean bb = (Boolean) get_Value("IsActive");
		if (bb != null)
			return bb.booleanValue();
		return false;
	} // isActive

	/**
	 * Get Created
	 * 
	 * @return created
	 */
	final public Timestamp getCreated() {
		return (Timestamp) get_Value("Created");
	} // getCreated

	/**
	 * Get Updated
	 * 
	 * @return updated
	 */
	final public Timestamp getUpdated() {
		return (Timestamp) get_Value("Updated");
	} // getUpdated

	/**
	 * Get CreatedBy
	 * 
	 * @return AD_User_ID
	 */
	final public int getCreatedBy() {
		Integer ii = (Integer) get_Value("CreatedBy");
		if (ii == null)
			return 0;
		return ii.intValue();
	} // getCreateddBy

	/**
	 * Get UpdatedBy
	 * 
	 * @return AD_User_ID
	 */
	final public int getUpdatedBy() {
		Integer ii = (Integer) get_Value("UpdatedBy");
		if (ii == null)
			return 0;
		return ii.intValue();
	} // getUpdatedBy

	/**
	 * Set UpdatedBy
	 * 
	 * @param AD_User_ID
	 *            user
	 */
	final protected void setUpdatedBy(int AD_User_ID) {
		set_ValueNoCheck("UpdatedBy", new Integer(AD_User_ID));
	} // setAD_User_ID

	public String get_Translation (String columnName)
	{
		return get_Translation(columnName, Env.getAD_Language(getCtx()));
	}

	
	/**
	 * Get Translation of column
	 * 
	 * @param columnName
	 * @param AD_Language
	 * @return translation or null if not found
	 */
	protected String get_Translation(String columnName, String AD_Language) {
		if (columnName == null || AD_Language == null || m_IDs.length > 1
				|| m_IDs[0].equals(I_ZERO) || !(m_IDs[0] instanceof Integer)) {
			log.severe("Invalid Argument: ColumnName" + columnName
					+ ", AD_Language=" + AD_Language + ", ID.length="
					+ m_IDs.length + ", ID=" + m_IDs[0]);
			return null;
		}
		int ID = ((Integer) m_IDs[0]).intValue();
		String retValue = null;
		StringBuffer sql = new StringBuffer("SELECT ").append(columnName)
				.append(" FROM ").append(p_info.getTableName()).append(
						"_Trl WHERE ").append(m_KeyColumns[0]).append("=?")
				.append(" AND AD_Language=?");
		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(sql.toString());
			pstmt.setInt(1, ID);
			pstmt.setString(2, AD_Language);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				retValue = rs.getString(1);
			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			log.log(Level.SEVERE, sql.toString(), e);
		}
		try {
			if (pstmt != null)
				pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}
		return retValue;
	} // get_Translation

	// Use PO method for persistence, or direct INSERT INTO clause from X_ 
	protected boolean directInsert = false;
	
	// Bajo direct insert, omitir incluso cualquier gestion de PO (como preliminares antes de la inserción o bien saveFinish)
	protected boolean skipHandlers = false;
	
	
	/**
	 * 	Is new record
	 *	@return true if new
	 */
	public boolean is_new()
	{
		if (m_createNew)
			return true;
		//
		for (int i = 0; i < m_IDs.length; i++)
		{
			if (m_IDs[i].equals(I_ZERO) || m_IDs[i] == NULL)
				continue;
			return false;	//	one value is non-zero
		}
		return true;
	}	//	is_new
	
	
	/**************************************************************************
	 * Update Value or create new record. To reload call load() - not updated
	 * 
	 * @return true if saved
	 */
	public boolean save() {
		// Insercion directa omitiendo cualquier tipo de logica?
		if (directInsert && skipHandlers) 
			return insertDirect();
		
		// New
		if (!m_createNew && m_IDs[0].equals(I_ZERO)) // first key value = 0
		{
			if (m_IDs.length > 1) // multi-key - one might be 0 (M_ASI)
			{
				if (m_IDs[1].equals(I_ZERO)) // but not two
					m_createNew = true;
			} else
				m_createNew = true;
		}
		boolean newRecord = m_createNew; // save locally as load resets
		if (!newRecord && !is_Changed()) {
			log.fine("Nothing changed - " + p_info.getTableName());
			return true;
		}

		// Org 0
		if (getAD_Org_ID() == 0
				&& M_Table.ACCESSLEVEL_Organization.equals(p_info
						.getAccessLevel())) {
			log.saveError("InvalidOrg", "");
			log.severe("AD_Org_ID=0 not valid for Org Access Table");
			return false;
		}
		// Before Save
		try {
			if (!handlePersistence(newRecord, false,
					new PluginPOBeforeSaveHandler())) {
				log.warning("beforeSave failed - " + toString());
				return false;
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "beforeSave - " + toString(), e);
			log.saveError("Error", e.toString(), false);
			// throw new DBException(e);
			return false;
		}
		String errorMsg = ModelValidationEngine.get().fireModelChange(
				this,
				newRecord ? ModelValidator.TYPE_NEW
						: ModelValidator.TYPE_CHANGE);
		if (errorMsg != null) {
			log.warning("Validation failed - " + errorMsg);
			log.saveError("Error", errorMsg);
			return false;
		}
		int index = p_info.getColumnIndexIgnoreCase("AD_ComponentVersion_ID");
		Integer componentVersionID = null;
		if (index > -1) {
			componentVersionID = (Integer) get_Value(index);
			// Asigna AD_ComponentVersion_ID al componente actual de desarrollo
			// si no tiene un valor asignado (solo para nuevos registros).
			if (m_createNew
					&& (componentVersionID == null || componentVersionID <= 0)) {
				MComponentVersion componentVersion = MComponentVersion
						.getCurrentComponentVersion(p_ctx, m_trxName);
				if (componentVersion != null) {
					componentVersionID = componentVersion
							.getAD_ComponentVersion_ID();
					set_ValueNoCheck("AD_ComponentVersion_ID",
							componentVersionID);
				}
			}

			// TODO: Descomentar estas líneas siguientes cuando
			// se actualicen los AD_ComponentVersion_ID de todas las tablas.
			// if(componentVersionID == null){
			// log.saveError("NotSaveWithoutComponentVersion", "");
			// return false;
			// }
		}
		// Save
		if (newRecord)
			return saveNew();
		else
			return saveUpdate();
	} // save

	/**
	 * Finish Save Process
	 * 
	 * @param newRecord
	 *            new
	 * @param success
	 *            success
	 * @return true if saved
	 */
	private boolean saveFinish(boolean newRecord, boolean success) {
		// Translations
		if (success) {
			if (newRecord)
				insertTranslations();
			else
				updateTranslations();
		}
		try {
			success = handlePersistence(newRecord, success,
					new PluginPOAfterSaveHandler());
		} catch (Exception e) {
			log.log(Level.SEVERE, "afterSave", e);
			log.saveError("Error", e.toString(), false);
			success = false;
			// throw new DBException(e);
		}
		// OK
		if (success) {
			if (s_docWFMgr == null) {
				try {
					Class.forName("org.openXpertya.wf.DocWorkflowManager");
				} catch (Exception e) {
				}
			}
			if (s_docWFMgr != null) {
				s_docWFMgr.process(this, p_info.getAD_Table_ID());
			}
			// Copy to Old values
			int size = p_info.getColumnCount();
			for (int i = 0; i < size; i++) {
				if (m_newValues[i] != null) {
					if (m_newValues[i] == NULL)
						m_oldValues[i] = null;
					else
						m_oldValues[i] = m_newValues[i];
				}
			}
			m_newValues = new Object[size];
		}
		m_createNew = false;
		if (!newRecord)
			CacheMgt.get().reset(p_info.getTableName());
		return success;
	} // saveFinish

	/**
	 * Update Value or create new record. To reload call load() - not updated
	 * 
	 * @param trxName
	 *            transaction
	 * @return true if saved
	 */
	public boolean save(String trxName) {
		set_TrxName(trxName);
		return save();
	} // save

	/**
	 * Is there a Change to be saved?
	 * 
	 * @return true if record changed
	 */
	public boolean is_Changed() {
		int size = get_ColumnCount();
		for (int i = 0; i < size; i++) {
			if (m_newValues[i] != null)
				return true; // something changed
		}
		return false;
	} // is_Change

	
	/**
	 * Invocaciones a métodos de ejecución rapida
	 * Redefinidas solo en ciertas subclases 
	 * @return
	 */
	protected boolean insertDirect()
	{
		// Si no está redefinido el método en la subclase, entonces
		// usar el metodo tradicional de persistencia. 
		// Previamente se setea a false el directInsert ya que
		// en caso contrario se entraría en una recursión indefinida:
		// saveNew() llama a insertDirect() e insertDirect() llama a saveNew()
		directInsert = false;
		return saveNew();
	}
	
	/**
	 * Called before Save for Pre-Save Operation
	 * 
	 * @param newRecord
	 *            new record
	 * @return true if record can be saved
	 */
	protected boolean beforeSave(boolean newRecord) {
		// log.saveError("Error", Msg.parseTranslation(getCtx(),
		// "@C_Currency_ID@ = @C_Currency_ID@"));
		return true;
	} // beforeSave

	/**
	 * Called after Save for Post-Save Operation
	 * 
	 * @param newRecord
	 *            new record
	 * @param success
	 *            true if save operation was success
	 * @return if save was a success
	 */
	protected boolean afterSave(boolean newRecord, boolean success) {
		return success;
	} // afterSave

	/**
	 * Update Record directly
	 * 
	 * @return true if updated
	 */
	protected boolean saveUpdate() {
		String where = get_WhereClause(false);
		//
		boolean changes = false;
		StringBuffer sql = new StringBuffer("UPDATE ");
		boolean updated = false;
		boolean updatedBy = false;

		CPreparedStatement consulta;
		LinkedList nuevosValores = new LinkedList();

		sql.append(p_info.getTableName()).append(" SET ");
		lobReset();
		int size = get_ColumnCount();
		for (int i = 0; i < size; i++) {
			if (p_info.isVirtualColumn(i))
				continue;
			// we have a change
			Object value = m_newValues[i];

			if (value != null) {
				Class c = p_info.getColumnClass(i);
				int dt = p_info.getColumnDisplayType(i);
				String columnName = p_info.getColumnName(i);
				//
				if (DisplayType.isLOB(dt)) {
					lobAdd(value, i, dt);
					value = null;
					// continue;
				}
				// Update Document No

				if (columnName.equals("DocumentNo")) {
					String strValue = (String) value;
					if (strValue.startsWith("<") && strValue.endsWith(">")) {
						value = null;
						int AD_Client_ID = getAD_Client_ID();
						int index = p_info.getColumnIndex("C_DocTypeTarget_ID");
						if (index == -1)
							index = p_info.getColumnIndex("C_DocType_ID");
						if (index != -1) // get based on Doc Type (might return
							// null)
							value = DB.getDocumentNo(get_ValueAsInt(index),
									m_trxName);
						if (value == null) // not overwritten by DocType and not
							// manually entered
							value = DB.getDocumentNo(AD_Client_ID, p_info
									.getTableName(), m_trxName);
					} else
						log.warning("DocumentNo updated: " + m_oldValues[i]
								+ " -> " + value);
				}
				
				// Value
				
				if (columnName.equals("Value")) {
					String strValue = (String)value;					
					if (strValue == null || strValue.trim().length() == 0) {
						value = get_ValueOld(i);
					}
				}

				if (changes)
					sql.append(", ");
				changes = true;
				sql.append(columnName).append(" = ? ");

				/*
				 * // values if (value == NULL) sql.append("NULL"); else if
				 * (value instanceof Integer || value instanceof BigDecimal)
				 * sql.append(value); else if (c == Boolean.class) { boolean
				 * bValue = false; if (value instanceof Boolean) bValue =
				 * ((Boolean)value).booleanValue(); else bValue =
				 * "Y".equals(value); sql.append(bValue ? "'Y'" : "'N'"); } else
				 * if (value instanceof Timestamp)
				 * sql.append(DB.TO_DATE((Timestamp
				 * )value,p_info.getColumnDisplayType(i) == DisplayType.Date));
				 * else sql.append(DB.TO_STRING(value.toString()));
				 */

				nuevosValores.add(new QueryParam(columnName, value, c, i, dt));

				// updated/by
				if (columnName.equals("UpdatedBy"))
					updatedBy = true;
				else if (columnName.equals("Updated"))
					updated = true;

				// // Change Log - Only
				// if (m_IDs.length == 1 && session != null)
				// {
				// Object oldV = m_oldValues[i];
				// Object newV = value;
				// if (oldV != null && oldV == NULL)
				// oldV = null;
				// if (newV != null && newV == NULL)
				// newV = null;
				// MChangeLog cLog = session.changeLog (
				// m_trxName, AD_ChangeLog_ID,
				// p_info.getAD_Table_ID(), p_info.getColumn(i).AD_Column_ID,
				// getID(), getAD_Client_ID(), getAD_Org_ID(), oldV, newV);
				// if (cLog != null)
				// AD_ChangeLog_ID = cLog.getAD_ChangeLog_ID();
				// }
			} // changed field
		} // for all fields

		// Custom Columns (cannot be logged as no column)
		if (m_custom != null) {
			Iterator it = m_custom.keySet().iterator();
			while (it.hasNext()) {
				if (changes)
					sql.append(", ");
				changes = true;
				//
				String column = (String) it.next();
				String value = (String) m_custom.get(column);
				sql.append(column).append("=").append(value);
			}
			m_custom = null;
		}

		// Something changed
		if (changes) {
			int no = 0;
			int colIdx = 1;

			if (m_trxName == null)
				log.fine(p_info.getTableName() + "." + where);
			else
				log.fine("[" + m_trxName + "] - " + p_info.getTableName() + "."
						+ where);

			if (!updated) // Updated not explicitly set
			{
				sql.append(", Updated = ? ");
			}

			if (!updatedBy) // UpdatedBy not explicitly set
			{
				sql.append(" , UpdatedBy = ? ");
			}

			sql.append(" WHERE ").append(where);
			/** @todo status locking goes here */

			consulta = DB.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					m_trxName);

			try {
				/*
				 * Establezco los valores de los parametros de la consulta
				 */
				Iterator it = nuevosValores.iterator();
				while (it.hasNext()) {
					QueryParam param = (QueryParam) it.next();

					setColToStatement(consulta, colIdx++, param);
				}

				/*
				 * Updated y UpdatedBy
				 */

				if (!updated) {
					Timestamp now = Env.getTimestamp();
					consulta.setTimestamp(colIdx++, now);
				}

				if (!updatedBy) {
					int AD_User_ID = Env.getContextAsInt(p_ctx, "#AD_User_ID");
					consulta.setInt(colIdx++, AD_User_ID);
				}

				/*
				 * Parametros del Where
				 */

				colIdx = setColToStatementWhere(consulta, colIdx);

				no = consulta.executeUpdate();

				if (m_trxName == null)
					consulta.commit();

				log.finest("Se actualizaron " + no + " registros.....");
			} catch (SQLException e) {
				log.log(Level.SEVERE, "saveUpdate", e + (consulta!=null?" - " + consulta.toString():""));
				log.saveError("Error", DB.getErrorMsg(e) + " - " + e);
				// e.printStackTrace();
			} catch (Exception e) {
				log.log(Level.SEVERE, "saveUpdate", e + (consulta!=null?" - " + consulta.toString():""));
				// e.printStackTrace();
			} finally {
				try {
					consulta.close();
				} catch (Exception e) {
					log.log(Level.SEVERE, "saveUpdate", e);
					// e.printStackTrace();
				}
			}

			boolean ok = no == 1;
			if (ok) {
				ok = logging(MChangeLog.OPERATIONTYPE_Modification);
			}
			if (ok) {
				ok = lobSave();
			} else {
				if (m_trxName == null)
					log.log(Level.SEVERE, p_info.getTableName() + "." + where + (consulta!=null?" - " + consulta.toString():""));
				else
					log.log(Level.SEVERE, "[" + m_trxName + "] - "
							+ p_info.getTableName() + "." + where + (consulta!=null?" - " + consulta.toString():""));
			}
			return saveFinish(false, ok);
		}

		// nothing changed, so OK
		log.finest("Vamos a retornar el saveFinish");
		return saveFinish(false, true);
	} // saveUpdate

	private int setColToStatementWhere(PreparedStatement pp, int colIdx)
			throws SQLException {
		for (int i = 0; i < m_IDs.length; i++) {
			if (m_KeyColumns[i].endsWith("_ID"))
				pp.setInt(colIdx++, ((Integer) m_IDs[i]).intValue());
			else
				pp.setObject(colIdx++, m_IDs[i]);
		}
		return colIdx;
	}

	private void setColToStatement(PreparedStatement pp,
			int statementColumnIndex, QueryParam param) throws SQLException {
		setColToStatement(pp, statementColumnIndex, param.getColumnName(),
				param.getColumnIndex(), param.getColumnDataType(), param
						.getValueClass(), param.getValue());
	}

	private void setColToStatement(PreparedStatement pp,
			int statementColumnIndex, String columnName, Object value)
			throws SQLException {
		int columnIndex = p_info.getColumnIndex(columnName);
		int dt = -1;
		Class c = null;

		if (columnIndex != -1) {
			dt = p_info.getColumnDisplayType(columnIndex);
			c = p_info.getColumnClass(columnIndex);
		} else {
			c = value.getClass();
		}

		setColToStatement(pp, statementColumnIndex, columnName, columnIndex,
				dt, c, value);
	}

	private Object CastToJdbcType(Object value, int jdbcType)
			throws SQLException {
		try {
			switch (jdbcType) {
			case Types.BIGINT:
			case Types.NUMERIC:
			case Types.REAL:
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
				return (value instanceof BigDecimal) ? value : new BigDecimal(
						value.toString());

			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.TINYINT:
				return (value instanceof Integer) ? value : new Integer(value
						.toString());

			case Types.VARCHAR:
			case Types.CHAR:
			case Types.LONGVARCHAR:
				return value.toString();

			case Types.DATE:
				return (Date) value;

			case Types.TIME:
				return (Time) value;

			case Types.TIMESTAMP:
				return (Timestamp) value;

			case Types.JAVA_OBJECT:
				return (SQLData) value;

			default:
				log.log(Level.SEVERE, "CastToJdbcType: tipo sin definir");
				return value;
			}
		} catch (NumberFormatException e) {
			log.log(Level.SEVERE, "CastToJdbcType: value: " + value.toString()
					+ " jdbcType: " + jdbcType, e);
			throw e;
		}
	}

	private void setColToStatement(PreparedStatement pp,
			int statementColumnIndex, String columnName, int columnIndex,
			int dt, Class c, Object value) throws SQLException {
		if (value == null || value.equals(NULL)) {
			pp.setNull(statementColumnIndex, Types.NULL);
			return;
		}

		if (DisplayType.isLookup(dt)) {
			/*
			ResultSet rs = pp.getConnection().getMetaData().getColumns(null,
					null, p_info.getTableName().toLowerCase(),
					p_info.getColumnName(columnIndex).toLowerCase());
			if (rs.next()) {
				int jdbcType = rs.getInt("DATA_TYPE");
				int colSize = rs.getInt("COLUMN_SIZE");
				Object x = CastToJdbcType(value, jdbcType);

				if (x.getClass() == String.class
						&& x.toString().length() > colSize)
					log.log(Level.SEVERE, "length() > colSize");

				pp.setObject(statementColumnIndex, x, jdbcType);
				return;
			}
			*/
			//Ader: CacheMetadata
			ResultColumnMetadata cMetadata = 
				CacheMetadata.DefInst().getForColumn(pp.getConnection(), 
				p_info.getTableName(), p_info.getColumnName(columnIndex));
			if (cMetadata != null)
			{
				int jdbcType = cMetadata.DATA_TYPE;
				int colSize = cMetadata.COLUMN_SIZE;
				Object x = CastToJdbcType(value, jdbcType);

				if (x.getClass() == String.class
						&& x.toString().length() > colSize)
					log.log(Level.SEVERE, "length() > colSize");

				pp.setObject(statementColumnIndex, x, jdbcType);
				return;
				
			}else
			{	
				log.log(Level.SEVERE, "No metadada found for column lookup: " + 
					p_info.getTableName() + "." +p_info.getColumnName(columnIndex) );
			}
		}

		if (dt == DisplayType.Binary)
			pp.setBytes(statementColumnIndex, (byte[]) value);
		else if (c == Object.class) // may have need to deal with null values
			// differently
			pp.setString(statementColumnIndex, (saveNewSpecial(value,
					columnIndex)));
		else if (value instanceof Integer)
			pp.setInt(statementColumnIndex, ((Integer) value).intValue());
		else if (value instanceof BigDecimal)
			pp.setBigDecimal(statementColumnIndex, (BigDecimal) value);
		else if (c == Boolean.class) {
			boolean bValue = false;
			if (value instanceof Boolean)
				bValue = ((Boolean) value).booleanValue();
			else
				bValue = "Y".equals(value);
			pp.setString(statementColumnIndex, (bValue ? "Y" : "N"));
		} else if (value instanceof Timestamp)
			pp.setTimestamp(statementColumnIndex, (Timestamp) value); // ((DB.TO_DATE
		// ((Timestamp)value,
		// p_info.getColumnDisplayType
		// (columnIndex)
		// ==
		// DisplayType.Date)));
		else if (c == String.class)
			pp.setString(statementColumnIndex, (String) value);
		else if (DisplayType.isLOB(dt))
			pp.setNull(statementColumnIndex, Types.NULL); // no db dependent
		// stuff here
		else
			pp.setString(statementColumnIndex, (saveNewSpecial(value,
					columnIndex)));

	}

	/**
	 * Create New Record
	 * 
	 * @return true if new record inserted
	 */
	private boolean saveNew() {
		// Set ID for single key - Multi-Key values need explicitly be set
		// previously
		if (m_IDs.length == 1 && p_info.hasKeyColumn()
				&& !m_KeyColumns[0].equals("AD_Language")) {
			int no = DB.getNextID(getAD_Client_ID(), p_info.getTableName(),
					m_trxName);
			if (no <= 0) {
				log.severe("No NextID (" + no + ")");
				return saveFinish(true, false);
			}
			m_IDs[0] = new Integer(no);
			set_ValueNoCheck(m_KeyColumns[0], m_IDs[0]);
		}
		if (m_trxName == null)
			log.fine(p_info.getTableName() + " - " + get_WhereClause(true));
		else
			log.fine("[" + m_trxName + "] - " + p_info.getTableName() + " - "
					+ get_WhereClause(true));

		// Set new DocumentNo
		String columnName = "DocumentNo";
		int index = p_info.getColumnIndex(columnName);
		if (index != -1) {
			String value = (String) get_Value(index);
			if (value != null && value.startsWith("<") && value.endsWith(">"))
				value = null;
			if (value == null || value.length() == 0) {
				int AD_Client_ID = getAD_Client_ID();
				int dt = p_info.getColumnIndex("C_DocTypeTarget_ID");
				if (dt == -1)
					dt = p_info.getColumnIndex("C_DocType_ID");
				if (dt != -1) // get based on Doc Type (might return null)
					value = DB.getDocumentNo(get_ValueAsInt(dt), m_trxName);
				if (value == null) // not overwritten by DocType and not
					// manually entered
					value = DB.getDocumentNo(AD_Client_ID, p_info
							.getTableName(), m_trxName);
				set_ValueNoCheck(columnName, value);
			}
		}
		// Set empty Value
		columnName = "Value";
		index = p_info.getColumnIndex(columnName);
		if (index != -1) {
			String value = (String) get_Value(index);
			if (value == null || value.trim().length() == 0) {
				value = DB.getDocumentNo(getAD_Client_ID(), p_info
						.getTableName(), m_trxName);
				set_ValueNoCheck(columnName, value);
			}
		}

		// Actualizar el campo AD_ComponentObjectUID
		if (!get_TableName().equalsIgnoreCase("AD_ChangeLog")) {
			// Actualizar el campo AD_ComponentObjectUID
			makeAndSetComponentObjectUID();
		}

		lobReset();

		/*
		 * Luego de los seteos adicionales al inicio del saveNew(),
		 * se verifica si el miembro directInsert fue seteado a true,
		 * en este caso, se omite la determinación dinámica de campos
		 * para la generación del query, y en su lugar se invoca al
		 * insertDirect que tenga implementado la subclase (en principio
		 * será el generado automaticamente por el GenerateModel sobre
		 * la clase X_ correspondiente), con la consulta previamente armada.
		 */
		if (directInsert)
		{
			boolean ok = insertDirect();
			return saveFinish(true, ok);
		}

		
		// SQL
		StringBuffer sqlInsert = new StringBuffer("INSERT INTO ");
		sqlInsert.append(p_info.getTableName()).append(" (");
		StringBuffer sql = new StringBuffer(") VALUES (");

		CPreparedStatement consulta;

		int colIdx = -1;
		Object value = new Object();

		try {
			LinkedList valoresNuevos = new LinkedList();
			Iterator it;
			int size = get_ColumnCount();
			boolean doComma = false;

			/*
			 * 1. Recolecto las columnas nuevas
			 */

			for (colIdx = 0; colIdx < size; colIdx++) {

				value = get_Value(colIdx);
				columnName = p_info.getColumnName(colIdx);
				int dt = p_info.getColumnDisplayType(colIdx);
				Class c = p_info.getColumnClass(colIdx);

				// Don't insert NULL values (allows Database defaults)
				if (value == null)
					continue;

				// Display Type
				if (DisplayType.isLOB(dt)) {
					lobAdd(value, colIdx, dt);
					value = null;
				}

				valoresNuevos.add(new QueryParam(columnName, value, c, colIdx,
						dt));
			}

			/*
			 * 2. Recolecto las columnas personalizadas
			 * 
			 * Por el momento (y hasta que no quede mas limpio el codigo) se
			 * ingresarán los valores literales en vez de usar las facilidades
			 * del preparedstatement.
			 */

			if (m_custom != null) {
				it = m_custom.keySet().iterator();
				while (it.hasNext()) {
					columnName = (String) it.next();
					value = (String) m_custom.get(columnName);

					if (doComma) {
						sqlInsert.append(",");
						sql.append(",");
					} else {
						doComma = true;
					}

					sqlInsert.append(columnName);
					sql.append(value);

					// valoresNuevos.put(columnName, value);
				}
				m_custom = null;
			}

			/*
			 * 3. Genero el query SQL
			 */

			it = valoresNuevos.iterator();
			while (it.hasNext()) {
				columnName = ((QueryParam) it.next()).getColumnName();
				if (doComma) {
					sqlInsert.append(",");
					sql.append(",");
				} else {
					doComma = true;
				}

				sqlInsert.append(columnName);
				sql.append("?");
			}

			// Concateno el string

			sqlInsert.append(sql).append(")");
			sql = null;

			// Genero el PreparedStatement

			consulta = DB.prepareStatement(sqlInsert.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					m_trxName);

			// Asigno los parametros de la consulta

			colIdx = 1;
			it = valoresNuevos.iterator();
			while (it.hasNext()) {
				QueryParam param = (QueryParam) it.next();

				setColToStatement(consulta, colIdx++, param);
			}
		} catch (Exception e) {
			String msg = "";
			if (m_trxName != null)
				msg = "[" + m_trxName + "] - ";
			msg += p_info.toString(colIdx) + " - Value=" + value + "("
					+ (value == null ? "null" : value.getClass().getName())
					+ ")";
			log.log(Level.SEVERE, msg, e);
			throw new DBException(e); // fini
		}

		/*
		 * for (int i = 0; i < size; i++) {
		 * 
		 * Object value = get_Value(i); // Don't insert NULL values (allows
		 * Database defaults) if (value == null) continue;
		 * 
		 * // Display Type int dt = p_info.getColumnDisplayType(i); if
		 * (DisplayType.isLOB(dt)) lobAdd (value, i, dt);
		 * 
		 * // ** add column ** if (doComma) { sqlInsert.append(",");
		 * sql.append(","); } else doComma = true;
		 * sqlInsert.append(p_info.getColumnName(i)); // // Based on class of
		 * definition, not class of value Class c = p_info.getColumnClass(i);
		 * try { if (c == Object.class) // may have need to deal with null
		 * values differently sql.append (saveNewSpecial (value, i)); else if
		 * (value == null || value.equals (NULL)) sql.append ("NULL"); else if
		 * (value instanceof Integer || value instanceof BigDecimal) sql.append
		 * (value); else if (c == Boolean.class) { boolean bValue = false; if
		 * (value instanceof Boolean) bValue = ((Boolean)value).booleanValue();
		 * else bValue = "Y".equals(value); sql.append (bValue ? "'Y'" : "'N'");
		 * } else if (value instanceof Timestamp) sql.append (DB.TO_DATE
		 * ((Timestamp)value, p_info.getColumnDisplayType (i) ==
		 * DisplayType.Date)); else if (c == String.class) sql.append
		 * (DB.TO_STRING ((String)value)); else if (DisplayType.isLOB(dt))
		 * sql.append("null"); // no db dependent stuff here else sql.append
		 * (saveNewSpecial (value, i)); } catch (Exception e) { String msg = "";
		 * if (m_trxName != null) msg = "[" + m_trxName + "] - "; msg +=
		 * p_info.toString(i) + " - Value=" + value + "(" + (value==null ?
		 * "null" : value.getClass().getName()) + ")"; log.log(Level.SEVERE,
		 * msg, e); throw new DBException(e); // fini } }
		 */

		/*
		 * // Custom Columns if (m_custom != null) { Iterator it =
		 * m_custom.keySet().iterator(); while (it.hasNext()) { String column =
		 * (String)it.next(); String value = (String)m_custom.get(column); if
		 * (doComma) { sqlInsert.append(","); sql.append(","); } else doComma =
		 * true; sqlInsert.append(column); sql.append(value); } m_custom = null;
		 * }
		 */

		int no = 0;

		try {
			no = consulta.executeUpdate();

			if (m_trxName == null)
				consulta.commit();
		} catch (SQLException e) {
			log.log(Level.SEVERE, "saveNew", e + (consulta!=null?" - " + consulta.toString():""));
			log.saveError("Error", DB.getErrorMsg(e) + " - " + e);
			// e.printStackTrace();
		} finally {
			try {
				consulta.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		boolean ok = no == 1;
		if (ok) {
			ok = logging(MChangeLog.OPERATIONTYPE_Insertion);
		}
		if (ok) {
			ok = lobSave();
			if (!load(m_trxName)) // re-read Info
			{
				if (m_trxName == null)
					log.log(Level.SEVERE, sqlInsert.toString());
				else
					log.log(Level.SEVERE, "[" + m_trxName + "] - "
							+ sqlInsert.toString());
				ok = false;
				;
			}
		} else {
			if (m_trxName == null)
				log.log(Level.SEVERE, "Not inserted - " + (consulta!=null?" - " + consulta.toString():"")); // sqlInsert.toString());
			else
				log.log(Level.SEVERE, "[" + m_trxName + "] - Not inserted - "
						 + (consulta!=null?" - " + consulta.toString():""));  // sqlInsert.toString());
		}
		return saveFinish(true, ok);
	} // saveNew

	/**
	 * Create Single/Multi Key Where Clause
	 * 
	 * @param withValues
	 *            if true uses actual values otherwise ?
	 * @return where clause
	 */
	public String get_WhereClause(boolean withValues) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < m_IDs.length; i++) {
			if (i != 0)
				sb.append(" AND ");
			sb.append(m_KeyColumns[i]).append("=");
			if (withValues) {
				if (m_KeyColumns[i].endsWith("_ID"))
					sb.append(m_IDs[i]);
				else
					sb.append("'").append(m_IDs[i]).append("'");
			} else
				sb.append("?");
		}
		return sb.toString();
	} // getWhereClause

	/**
	 * Save Special Data. To be extended by sub-classes
	 * 
	 * @param value
	 *            value
	 * @param index
	 *            index
	 * @return SQL code for INSERT VALUES clause
	 */
	protected String saveNewSpecial(Object value, int index) {
		String colName = p_info.getColumnName(index);
		String colClass = p_info.getColumnClass(index).toString();
		String colValue = value == null ? "null" : value.getClass().toString();
		int dt = p_info.getColumnDisplayType(index);

		log.log(Level.SEVERE, "Unknown class for column " + colName + " ("
				+ colClass + ") - Value=" + colValue);

		if (value == null)
			return "NULL";
		return value.toString();
	} // saveNewSpecial

	/**************************************************************************
	 * Delete Current Record
	 * 
	 * @param force
	 *            delete also processed records
	 * @return true if deleted
	 */
	public boolean delete(boolean force) {
		if (m_createNew || m_IDs[0].equals(I_ZERO)) // new
			return true;

		if (!force) {
			int iProcessed = get_ColumnIndex("Processed");
			if (iProcessed != -1) {
				Boolean processed = (Boolean) get_Value(iProcessed);
				if (processed != null && processed.booleanValue()) {
					log.warning("Record processed");
					log.saveError("RecordProcessedDeleteError", "", false);
					return false;
				}
			} // processed
		} // force

		try {
			if (!handlePersistence(false, false,
					new PluginPOBeforeDeleteHandler())) {
				log.warning("beforeDelete failed");
				return false;
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "beforeDelete", e);
			log.saveError("Error", e.toString(), false);
			// throw new DBException(e);
			return false;
		}
		String errorMsg = ModelValidationEngine.get().fireModelChange(this,
				ModelValidator.TYPE_DELETE);
		if (errorMsg != null) {
			log.saveError("Error", errorMsg);
			return false;
		}

		// Prepare Delete Attachment
		StringBuffer attachment = new StringBuffer(
				"DELETE FROM AD_Attachment WHERE AD_Table_ID=").append(
				p_info.getAD_Table_ID()).append(" AND Record_ID=").append(
				m_IDs[0]);

		// The Delete Statement
		StringBuffer sql = new StringBuffer("DELETE ").append(
				p_info.getTableName()).append(" WHERE ").append(
				get_WhereClause(false));
		//
		deleteTranslations();

		CPreparedStatement consulta1 = DB.prepareStatement(sql.toString(),
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
				m_trxName);
		int colIdx = 1;
		int no = 0;

		errorMsg = "";

		try {
			colIdx = setColToStatementWhere(consulta1, colIdx);
			no = consulta1.executeUpdate();

			if (m_trxName == null)
				consulta1.commit();
		} catch (SQLException e) {
			errorMsg = DB.getErrorMsg(e) + " - " + e;
			e.printStackTrace();
			log.log(Level.SEVERE, "saveUpdate", e);
			log.saveError("Error", errorMsg);

		} finally {
			try {
				consulta1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Save ID
		m_idOld = getID();
		boolean success = no == 1;
		//
		if (success) {
			// // Change Log
			// MSession session = MSession.get (p_ctx, false);
			// if (session == null)
			// log.fine("No Session found");
			// else if (m_IDs.length == 1 &&
			// MChangeLog.isLogged(p_info.getAD_Table_ID()))
			// {
			// int AD_ChangeLog_ID = 0;
			// int size = get_ColumnCount();
			// for (int i = 0; i < size; i++)
			// {
			// Object value = m_oldValues[i];
			// if (value != null)
			// {
			// MChangeLog cLog = session.changeLog (
			// m_trxName, AD_ChangeLog_ID,
			// p_info.getAD_Table_ID(), p_info.getColumn(i).AD_Column_ID,
			// getID(), getAD_Client_ID(), getAD_Org_ID(), value, null);
			// if (cLog != null)
			// AD_ChangeLog_ID = cLog.getAD_ChangeLog_ID();
			// }
			// } // for all fields
			// }

			// Change Log
			success = logging(MChangeLog.OPERATIONTYPE_Deletion);

			// Delete Attachments
			if (m_IDs.length == 1) {
				DB.executeUpdate(attachment.toString(), m_trxName);
			}

			// Housekeeping
			m_IDs[0] = I_ZERO;
			if (m_trxName == null)
				log.fine("complete");
			else
				log.fine("[" + m_trxName + "] - complete");
		} else {
			/*
			 * Disytel - Matias Cap Se decidió salvar el error ya que si llama a
			 * warning y hay algún error guardado con anterioridad no hace nada
			 * y muestra el error guardado con anterioridad. Además, si no se
			 * pudo eliminar porque ocurrió un error, debería ser un error antes
			 * que un warning
			 */
			log.saveError("DeleteError", errorMsg);
			// log.warning("Not deleted");

			// Fin modificación Disytel - Matias Cap
		}
		m_attachment = null;

		try {
			success = handlePersistence(false, success,
					new PluginPOAfterDeleteHandler());
		} catch (Exception e) {
			log.log(Level.SEVERE, "afterDelete", e);
			log.saveError("Error", e.toString(), false);
			success = false;
			// throw new DBException(e);
		}
		// Reset
		m_idOld = 0;
		int size = p_info.getColumnCount();
		m_oldValues = new Object[size];
		m_newValues = new Object[size];
		CacheMgt.get().reset(p_info.getTableName());
		return success;
	} // delete

	/**
	 * Delete Current Record
	 * 
	 * @param force
	 *            delete also processed records
	 * @param trxName
	 *            transaction
	 */
	public boolean delete(boolean force, String trxName) {
		set_TrxName(trxName);
		return delete(force);
	} // delete

	/**
	 * Executed before Delete operation.
	 * 
	 * @return true if record can be deleted
	 */
	protected boolean beforeDelete() {
		// log.saveError("Error", Msg.getMsg(getCtx(), "CannotDelete"));
		return true;
	} // beforeDelete

	/**
	 * Executed after Delete operation.
	 * 
	 * @param success
	 *            true if record deleted
	 * @return true if delete is a success
	 */
	protected boolean afterDelete(boolean success) {
		return success;
	} // afterDelete

	/**
	 * Insert (missing) Translation Records
	 * 
	 * @returns false if error (true if no translation or success)
	 */
	private boolean insertTranslations() {
		// Not a translation table
		if (m_IDs.length > 1 || m_IDs[0].equals(I_ZERO)
				|| !p_info.isTranslated() || !(m_IDs[0] instanceof Integer))
			return true;
		//
		StringBuffer iColumns = new StringBuffer();
		StringBuffer sColumns = new StringBuffer();
		for (int i = 0; i < p_info.getColumnCount(); i++) {
			if (p_info.isColumnTranslated(i)) {
				iColumns.append(p_info.getColumnName(i)).append(",");
				sColumns.append("t.").append(p_info.getColumnName(i)).append(
						",");
			}
		}
		if (iColumns.length() == 0)
			return true;

		String tableName = p_info.getTableName();
		String keyColumn = m_KeyColumns[0];

		// Si la tabla de traducción tiene la columna AD_ComponentVersion_ID
		// entonces se asigna el valor desde la tabla original.
		String trlTableName = tableName + "_Trl";
		Integer columnID = (Integer) DB
				.getSQLObject(
						get_TrxName(),
						"SELECT AD_Column_ID "
								+ "FROM AD_Column c "
								+ "INNER JOIN AD_Table t ON (c.AD_Table_ID = t.AD_Table_ID) "
								+ "WHERE UPPER(t.TableName) = ? AND UPPER(c.ColumnName) = UPPER('AD_ComponentVersion_ID')",
						new Object[] { trlTableName.toUpperCase() });
		boolean hasComponentVersionID = columnID != null;
		if (hasComponentVersionID) {
			iColumns.append("AD_ComponentVersion_ID,");
			sColumns.append("t.AD_ComponentVersion_ID,");
		}

		StringBuffer sql = new StringBuffer(" ")
				.append("SELECT l.AD_Language,t.")
				.append(keyColumn)
				.append(", ")
				.append(sColumns)
				.append(
						" 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy ")
				.append("FROM AD_Language l, ")
				.append(tableName)
				.append(" t ")
				.append(
						"WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.")
				.append(keyColumn).append("=").append(getID()).append(
						" AND NOT EXISTS (SELECT * FROM ").append(tableName)
				.append("_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.")
				.append(keyColumn).append("=t.").append(keyColumn).append(")");

		/* Determinar el ID de la tabla de traducción */
		int trlTableID = DB.getSQLValue(m_trxName,
				" SELECT AD_Table_ID FROM AD_Table WHERE tablename ilike '"
						+ trlTableName + "'");
		M_Table table = new M_Table(p_ctx, trlTableID, m_trxName);

		/* Error al determinar la tabla correspondiente */
		if (trlTableID == -1 || table == null)
			return false;

		try {
			/* Iterar por cada traducción a crear */
			PreparedStatement stmt = DB.prepareStatement(sql.toString(),
					m_trxName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				/* Nueva instancia de traducción */
				PO trlPO = table.getPO(0, m_trxName);

				/* Campos fijos */
				trlPO.set_Value("IsTranslated", "N");
				trlPO.set_Value("AD_Language", rs.getString("AD_Language"));
				trlPO.set_Value(keyColumn, rs.getInt(keyColumn));

				/* Campos variables */
				String[] fields = iColumns.toString().split(",");
				for (String field : fields) {
					/*
					 * Si es el campo AD_ComponentVersion_ID debe recuperar un
					 * entero, en caso contrario sera un String
					 */
					if ("AD_ComponentVersion_ID".equalsIgnoreCase(field))
						trlPO.set_Value(field, rs.getInt(field));
					else
						trlPO.set_Value(field, rs.getString(field));
				}

				/* Almacenar la nueva traducción */
				if (!trlPO.save())
					return false;
			}

		} catch (Exception e) {
			log.log(Level.WARNING, " Translation Exception - TrlTableName: "
					+ trlTableName + " - " + e.getMessage());
			return false;
		}

		return true;
	} // insertTranslations

	/**
	 * Update Translations.
	 * 
	 * @returns false if error (true if no translation or success)
	 */
	private boolean updateTranslations() {
		// Not a translation table
		if (m_IDs.length > 1 || m_IDs[0].equals(I_ZERO)
				|| !p_info.isTranslated() || !(m_IDs[0] instanceof Integer))
			return true;
		//
		boolean trlColumnChanged = false;
		for (int i = 0; i < p_info.getColumnCount(); i++) {
			if (p_info.isColumnTranslated(i)
					&& is_ValueChanged(p_info.getColumnName(i))) {
				trlColumnChanged = true;
				break;
			}
		}
		if (!trlColumnChanged)
			return true;
		//
		MClient client = MClient.get(getCtx());
		//
		String tableName = p_info.getTableName();
		String keyColumn = m_KeyColumns[0];
		StringBuffer sql = new StringBuffer("UPDATE ").append(tableName)
				.append("_Trl SET ");
		//
		if (client.isAutoUpdateTrl(tableName)) {
			for (int i = 0; i < p_info.getColumnCount(); i++) {
				if (p_info.isColumnTranslated(i)) {
					String columnName = p_info.getColumnName(i);
					sql.append(columnName).append("=");
					Object value = get_Value(columnName);
					if (value == null)
						sql.append("NULL");
					else if (value instanceof String)
						sql.append(DB.TO_STRING((String) value));
					else if (value instanceof Boolean)
						sql.append(((Boolean) value).booleanValue() ? "'Y'"
								: "'N'");
					else if (value instanceof Timestamp)
						sql.append(DB.TO_DATE((Timestamp) value));
					else
						sql.append(value.toString());
					sql.append(",");
				}
			}
			sql.append("IsTranslated='Y'");
		} else
			sql.append("IsTranslated='N'");
		//
		sql.append(" WHERE ").append(keyColumn).append("=").append(getID());
		int no = DB.executeUpdate(sql.toString(), m_trxName);
		log.fine("#" + no);
		return no >= 0;
	} // updateTranslations

	/**
	 * Delete Translation Records
	 * 
	 * @returns false if error (true if no translation or success)
	 */
	private boolean deleteTranslations() {
		// Not a translation table
		if (m_IDs.length > 1 || m_IDs[0].equals(I_ZERO)
				|| !p_info.isTranslated() || !(m_IDs[0] instanceof Integer))
			return true;
		//
		String tableName = get_TableName();
		String keyColumn = m_KeyColumns[0];
		List<PO> trls = PO.find(getCtx(), get_TableName() + "_trl", keyColumn
				+ "= ?", new Object[] { getID() }, null, get_TrxName());
		for (PO trl : trls) {
			if (!trl.delete(false, get_TrxName())) {
				log.severe("Error al eliminar las traducciones de la tabla "
						+ tableName);
			}
		}
		return true;
	} // deleteTranslations

	/**
	 * Insert Accounting Records
	 * 
	 * @param acctTable
	 *            accounting sub table
	 * @param acctBaseTable
	 *            acct table to get data from
	 * @param whereClause
	 *            optional where clause with alias "p" for acctBaseTable
	 * @return true if records inserted
	 */
	public boolean insert_Accounting(String acctTable, String acctBaseTable,
			String whereClause) {
		if (s_acctColumns == null // cannot cache C_BP_*_Acct as there are 3
				|| acctTable.startsWith("C_BP_")) {
			s_acctColumns = new ArrayList();
			String sql = "SELECT c.ColumnName "
					+ "FROM AD_Column c INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID) "
					+ "WHERE t.TableName=? AND c.IsActive='Y' AND c.AD_Reference_ID=25 ORDER BY 1";
			PreparedStatement pstmt = null;
			try {
				pstmt = DB.prepareStatement(sql);
				pstmt.setString(1, acctTable);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					s_acctColumns.add(rs.getString(1));
				rs.close();
				pstmt.close();
				pstmt = null;
			} catch (Exception e) {
				log.log(Level.SEVERE, acctTable, e);
			}
			try {
				if (pstmt != null)
					pstmt.close();
				pstmt = null;
			} catch (Exception e) {
				pstmt = null;
			}
			if (s_acctColumns.size() == 0) {
				log.severe("No Columns for " + acctTable);
				return false;
			}
		}

		// Para C_CashBook_Acct se deben insertar los registros utilizando el AD_Org_ID del C_CashBook
		String orgID = "0";
		if (X_C_CashBook_Acct.Table_Name.equalsIgnoreCase(acctTable))
			orgID = Integer.toString(getAD_Org_ID());
		
		// Create SQL Statement - INSERT
		StringBuffer sb = new StringBuffer("INSERT INTO ")
				.append(acctTable)
				.append(" (")
				.append(get_TableName())
				.append(
						"_ID, C_AcctSchema_ID, AD_Client_ID,AD_Org_ID,IsActive, Created,CreatedBy,Updated,UpdatedBy ");
		for (int i = 0; i < s_acctColumns.size(); i++)
			sb.append(",").append(s_acctColumns.get(i));
		// .. SELECT
		sb.append(") SELECT ").append(getID()).append(
				", p.C_AcctSchema_ID, p.AD_Client_ID,").append(orgID).append(",'Y', SysDate,").append(
				getUpdatedBy()).append(",SysDate,").append(getUpdatedBy());
		for (int i = 0; i < s_acctColumns.size(); i++)
			sb.append(",p.").append(s_acctColumns.get(i));
		// .. FROM
		sb.append(" FROM ").append(acctBaseTable).append(
				" p WHERE p.AD_Client_ID=").append(getAD_Client_ID());
		if (whereClause != null && whereClause.length() > 0)
			sb.append(" AND ").append(whereClause);
		sb.append(" AND NOT EXISTS (SELECT * FROM ").append(acctTable).append(
				" e WHERE e.C_AcctSchema_ID=p.C_AcctSchema_ID AND e.").append(
				get_TableName()).append("_ID=").append(getID()).append(")");
		//
		int no = DB.executeUpdate(sb.toString(), get_TrxName());
		if (no > 0)
			log.fine("#" + no);
		else
			log.warning("#" + no + " - Table=" + acctTable + " from "
					+ acctBaseTable + ". Sentence: "+sb.toString());
		return no > 0;
	} // insert_Accounting

	/**
	 * Delete Accounting records. NOP - done by database constraints
	 * 
	 * @param acctTable
	 *            accounting sub table
	 * @return true
	 */
	protected boolean delete_Accounting(String acctTable) {
		return true;
	} // delete_Accounting

	/**
	 * Insert id data into Tree
	 * 
	 * @param treeType
	 *            MTree TREETYPE_*
	 * @return true if inserted
	 */
	protected boolean insert_Tree(String treeType) {
		return insert_Tree(treeType, 0);
	} // insert_Tree

	/**
	 * Insert id data into Tree
	 * 
	 * @param treeType
	 *            MTree TREETYPE_*
	 * @param C_Element_ID
	 *            element for accounting element values
	 * @return true if inserted
	 */
	protected boolean insert_Tree(String treeType, int C_Element_ID) {
		StringBuffer sb = new StringBuffer(
				"SELECT t.AD_Client_ID,0, 'Y', SysDate, 0, SysDate, 0,"
						+ "t.AD_Tree_ID, ").append(getID()).append(
				", 0, 999 " + "FROM AD_Tree t " + "WHERE t.AD_Client_ID=")
				.append(getAD_Client_ID()).append(" AND t.IsActive='Y'");
		// Account Element Value handling
		if (C_Element_ID != 0)
			sb
					.append(
							" AND EXISTS (SELECT * FROM C_Element ae WHERE ae.C_Element_ID=")
					.append(C_Element_ID)
					.append(
							" AND (t.AD_Tree_ID=ae.AD_Tree_ID"
									+ " OR (t.AD_Tree_ID=ae.Add1Tree_ID AND t.IsAllNodes='Y')"
									+ " OR t.AD_Tree_ID=ae.Add2Tree_ID AND t.IsAllNodes='Y'))");
		else
			// std trees
			sb.append(" AND t.IsAllNodes='Y' AND t.TreeType='")
					.append(treeType).append("'");
		// Duplicate Check
		sb.append(
				" AND NOT EXISTS (SELECT * FROM "
						+ MTree_Base.getNodeTableName(treeType) + " e "
						+ "WHERE e.AD_Tree_ID=t.AD_Tree_ID AND Node_ID=")
				.append(getID()).append(")");

		try {
			/* Tabla tree correspondiente */
			int tableID = DB.getSQLValue(m_trxName,
					" SELECT AD_Table_ID FROM AD_Table WHERE tablename ilike '"
							+ MTree_Base.getNodeTableName(treeType) + "'");

			/* Crear las instancias y persistirlas */
			PreparedStatement pstmt = DB.prepareStatement(sb.toString());
			M_Table table = M_Table.get(getCtx(), tableID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int i = 1;
				PO nodePO = table.getPO(0, m_trxName);
				nodePO.set_Value("AD_Client_ID", rs.getInt(i++));
				nodePO.set_Value("AD_Org_ID", rs.getInt(i++));
				nodePO.set_Value("IsActive", rs.getString(i++));
				nodePO.set_Value("Created", rs.getTimestamp(i++));
				nodePO.set_Value("CreatedBy", rs.getInt(i++));
				nodePO.set_Value("Updated", rs.getTimestamp(i++));
				nodePO.set_Value("UpdatedBy", rs.getInt(i++));
				nodePO.set_Value("AD_Tree_ID", rs.getInt(i++));
				nodePO.set_Value("Node_ID", rs.getInt(i++));
				nodePO.set_Value("Parent_ID", rs.getInt(i++));
				nodePO.set_Value("SeqNo", new BigDecimal(rs.getInt(i++)));
				if (!nodePO.save())
					throw new Exception();
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, " Insert node error: ", e);
			return false;
		}

		return true;

	} // insert_Tree

	/**
	 * Delete ID Tree Nodes
	 * 
	 * @param treeType
	 *            MTree TREETYPE_*
	 * @return true if deleted
	 */
	protected boolean delete_Tree(String treeType) {
		// Si existe un árbol con este nodo,
		// elimino este nodo entonces del árbol
		String tableName = MTree_Base.getNodeTableName(treeType);
		List<PO> treeNodes = PO.find(getCtx(), tableName, "node_id = ?",
				new Object[] { m_idOld }, null, get_TrxName());
		for (PO node : treeNodes) {
			if (!node.delete(false, get_TrxName())) {
				log.severe("Error al eliminar los nodos del arbol");
			}
		}
		return true;
	} // delete_Tree

	/**************************************************************************
	 * Lock it.
	 * 
	 * @return true if locked
	 */
	public boolean lock() {
		int index = get_ProcessingIndex();
		if (index != -1) {
			m_newValues[index] = Boolean.TRUE; // direct
			String sql = "UPDATE "
					+ p_info.getTableName()
					+ " SET Processing='Y' WHERE (Processing='N' OR Processing IS NULL) AND "
					+ get_WhereClause(true);
			boolean success = DB.executeUpdate(sql, null) == 1; // outside trx
			if (success)
				log.fine("success");
			else
				log.log(Level.SEVERE, "failed");
			return success;
		}
		return false;
	} // lock

	/**
	 * Get the Column Processing index
	 * 
	 * @return index or -1
	 */
	private int get_ProcessingIndex() {
		return p_info.getColumnIndex("Processing");
	} // getProcessingIndex

	/**
	 * UnLock it
	 * 
	 * @return true if unlocked (false only if unlock fails)
	 */
	public boolean unlock() {
		int index = get_ProcessingIndex();
		if (index != -1) {
			m_newValues[index] = Boolean.FALSE; // direct
			String sql = "UPDATE " + p_info.getTableName()
					+ " SET Processing='N' WHERE " + get_WhereClause(true);
			boolean success = DB.executeUpdate(sql) == 1; // outside trx
			if (success)
				log.fine("success");
			else
				log.log(Level.SEVERE, "failed");
			return success;
		}
		return true;
	} // unlock

	/** Optional Transaction */
	private String m_trxName = null;

	/**
	 * Set Trx
	 * 
	 * @param trxName
	 *            transaction
	 */
	public void set_TrxName(String trxName) {
		m_trxName = trxName;
	} // setTrx

	/**
	 * Get Trx
	 * 
	 * @return transaction
	 */
	public String get_TrxName() {
		return m_trxName;
	} // getTrx

	/**************************************************************************
	 * Get Attachments. An attachment may have multiple entries
	 * 
	 * @return Attachment or null
	 */
	public MAttachment getAttachment() {
		return getAttachment(false);
	} // getAttachment

	/**
	 * Get Attachments
	 * 
	 * @param requery
	 *            requery
	 * @return Attachment or null
	 */
	public MAttachment getAttachment(boolean requery) {
		if (m_attachment == null || requery)
			m_attachment = MAttachment.get(getCtx(), p_info.getAD_Table_ID(),
					getID());
		return m_attachment;
	} // getAttachment

	/**
	 * Create/return Attachment for PO. If not exist, create new
	 * 
	 * @return attachment
	 */
	public MAttachment createAttachment() {
		getAttachment(false);
		if (m_attachment == null)
			m_attachment = new MAttachment(getCtx(), p_info.getAD_Table_ID(),
					getID(), null);
		return m_attachment;
	} // createAttachment

	/**
	 * Do we have a Attachment of type
	 * 
	 * @param extension
	 *            extension e.g. .pdf
	 * @return true if there is a attachment of type
	 */
	public boolean isAttachment(String extension) {
		getAttachment(false);
		if (m_attachment == null)
			return false;
		for (int i = 0; i < m_attachment.getEntryCount(); i++) {
			if (m_attachment.getEntryName(i).endsWith(extension)) {
				log.fine("#" + i + ": " + m_attachment.getEntryName(i));
				return true;
			}
		}
		return false;
	} // isAttachment

	/**
	 * Get Attachment Data of type
	 * 
	 * @param extension
	 *            extension e.g. .pdf
	 * @return data or null
	 */
	public byte[] getAttachmentData(String extension) {
		getAttachment(false);
		if (m_attachment == null)
			return null;
		for (int i = 0; i < m_attachment.getEntryCount(); i++) {
			if (m_attachment.getEntryName(i).endsWith(extension)) {
				log.fine("#" + i + ": " + m_attachment.getEntryName(i));
				return m_attachment.getEntryData(i);
			}
		}
		return null;
	} // getAttachmentData

	/**
	 * Do we have a PDF Attachment
	 * 
	 * @return true if there is a PDF attachment
	 */
	public boolean isPdfAttachment() {
		return isAttachment(".pdf");
	} // isPdfAttachment

	/**
	 * Get PDF Attachment Data
	 * 
	 * @return data or null
	 */
	public byte[] getPdfAttachment() {
		return getAttachmentData(".pdf");
	} // getPDFAttachment

	/**************************************************************************
	 * Dump Record
	 */
	public void dump() {
		if (CLogMgt.isLevelFinest()) {
			log.finer(get_WhereClause(true));
			for (int i = 0; i < get_ColumnCount(); i++)
				dump(i);
		}
	} // dump

	/**
	 * Dump column
	 * 
	 * @param index
	 *            index
	 */
	public void dump(int index) {
		StringBuffer sb = new StringBuffer(" ").append(index);
		if (index < 0 || index >= get_ColumnCount()) {
			log.finest(sb.append(": invalid").toString());
			return;
		}
		sb.append(": ").append(get_ColumnName(index)).append(" = ").append(
				m_oldValues[index]).append(" (").append(m_newValues[index])
				.append(")");
		log.finest(sb.toString());
	} // dump

	/*************************************************************************
	 * Get All IDs of Table. Used for listing all Entities <code>
	 	int[] IDs = PO.getAllIDs ("AD_PrintFont", null);
		for (int i = 0; i < IDs.length; i++)
		{
			pf = new MPrintFont(Env.getCtx(), IDs[i]);
			System.out.println(IDs[i] + " = " + pf.getFont());
		}
	 *	</code>
	 * 
	 * @param TableName
	 *            table name (key column with _ID)
	 * @param WhereClause
	 *            optional where clause
	 * @return array of IDs or null
	 */
	public static int[] getAllIDs(String TableName, String WhereClause,
			String trxName) {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(TableName).append("_ID FROM ").append(TableName);
		if (WhereClause != null && WhereClause.length() > 0)
			sql.append(" WHERE ").append(WhereClause);
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql.toString(),
					trxName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				list.add(new Integer(rs.getInt(1)));
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, sql.toString(), e);
			return null;
		}
		// Convert to array
		int[] retValue = new int[list.size()];
		for (int i = 0; i < retValue.length; i++)
			retValue[i] = ((Integer) list.get(i)).intValue();
		return retValue;
	} // getAllIDs

	/**
	 * Get Find parameter. Convert to upper case and add % at the end
	 * 
	 * @param query
	 *            in string
	 * @return out string
	 */
	protected static String getFindParameter(String query) {
		if (query == null)
			return null;
		if (query.length() == 0 || query.equals("%"))
			return null;
		if (!query.endsWith("%"))
			query += "%";
		return query.toUpperCase();
	} // getFindParameter

	/**************************************************************************
	 * Load LOB
	 * 
	 * @param value
	 *            LOB
	 */
	private Object get_LOB(Object value) {
		if (value == null)
			return null;
		//
		Object retValue = null;
		// begin vpj-cd e-Evolution 03/11/2005 PostgreSQL
		if (DB.isPostgreSQL()) {
			byte buf[] = (byte[]) value;
			retValue = buf;
			return retValue;
		}
		// end vpj-cd e-Evolution 03/11/2005 PostgreSQL
		long length = -99;
		try {
			if (value instanceof Clob) // returns String
			{
				Clob clob = (Clob) value;
				length = clob.length();
				retValue = clob.getSubString(1, (int) length);
			} else if (value instanceof Blob) // returns byte[]
			{
				Blob blob = (Blob) value;
				length = blob.length();
				int index = 1; // correct
				if (blob.getClass().getName().equals(
						"oracle.jdbc.rowset.OracleSerialBlob"))
					index = 0; // Oracle Bug Invalid Arguments
				// at
				// oracle.jdbc.rowset.OracleSerialBlob.getBytes(OracleSerialBlob.java:130)
				retValue = blob.getBytes(index, (int) length);
			} else
				log.log(Level.SEVERE, "Unknown: " + value);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Length=" + length, e);
		}
		return retValue;
	} // getLOB

	/** LOB Info */
	private ArrayList m_lobInfo = null;

	/**
	 * Reset LOB info
	 */
	private void lobReset() {
		m_lobInfo = null;
	} // resetLOB

	/**
	 * Prepare LOB save
	 * 
	 * @param value
	 *            value
	 * @param index
	 *            index
	 * @param displayType
	 *            display type
	 */
	private void lobAdd(Object value, int index, int displayType) {
		PO_LOB lob = new PO_LOB(p_info.getTableName(), get_ColumnName(index),
				get_WhereClause(true), displayType, value);
		if (m_lobInfo == null)
			m_lobInfo = new ArrayList();
		m_lobInfo.add(lob);
	} // lobAdd

	/**
	 * Save LOB
	 * 
	 * @return true if saved or ok
	 */
	private boolean lobSave() {
		if (m_lobInfo == null)
			return true;
		boolean retValue = true;
		for (int i = 0; i < m_lobInfo.size(); i++) {
			PO_LOB lob = (PO_LOB) m_lobInfo.get(i);
			if (!lob.save(get_TrxName())) {
				retValue = false;
				break;
			}
		} // for all LOBs
		lobReset();
		return retValue;
	} // saveLOB

	public boolean exportElement2FieldTranslations(int AD_Field_ID,
			int AD_Column_ID) {

		PreparedStatement pstmt = null;

		try {

			/* Realizar el update de las traducciones que ya existen */
			/* ----------------------------------------------------- */
			String sql = "select l.AD_Language, l.AD_Element_ID, l.name, "
					+ " l.printname, l.description, l.Help, l.IsTranslated, "
					+ " l.ad_client_id, l.ad_org_id,l.Created,l.Createdby,l.Updated,l.UpdatedBy "
					+ " from ad_Element_trl l, ad_element e, ad_column c "
					+ " where c.ad_column_id = ? "
					+ " and c.ad_element_id = e.ad_element_id "
					+ " and e.ad_element_id = l.ad_element_id "
					+ " and l.ad_language in (select trl.ad_language from ad_field_trl trl, ad_field fi"
					+ " where trl.ad_field_id = fi.ad_field_id"
					+ " and  fi.ad_field_id = ?)";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, AD_Column_ID);
			pstmt.setInt(2, AD_Field_ID);
			ResultSet rs = pstmt.executeQuery();

			PreparedStatement pstmtTrl = null;
			ResultSet rsTrl = null;
			while (rs.next()) {
				pstmtTrl = DB.prepareStatement(
						" SELECT * FROM AD_Field_Trl WHERE AD_Language = '"
								+ rs.getString("AD_Language")
								+ "' AND AD_Field_ID = " + AD_Field_ID,
						get_TrxName());
				rsTrl = pstmtTrl.executeQuery();

				if (rsTrl.next()) {
					X_AD_Field_Trl trl = new X_AD_Field_Trl(getCtx(), rsTrl,
							get_TrxName());
					trl.setDescription(rs.getString("Description") != null ? rs
							.getString("Description") : "");
					trl.setHelp(rs.getString("Help") != null ? rs
							.getString("Help") : "");
					trl.setName(rs.getString("name"));
					trl.setIsTranslated((rs.getBoolean("IsTranslated")));
					if (!trl.save())
						log.log(Level.WARNING, "Field_Trl not exported!");
				}

			}

			if (rsTrl != null)
				rsTrl.close();
			if (pstmtTrl != null)
				pstmtTrl.close();
			pstmtTrl = null;

			/*
			 * Realiza el insert de los idiomas que no se hayan agregado y
			 * tengan traduccion en ad_element_trl
			 */
			/*
			 * ------------------------------------------------------------------
			 * -----------------------------
			 */

			String sqlInsert = "	select l.ad_language , "
					+ AD_Field_ID
					+ ", l.description, l.Help, l.name, "
					+ "		l.IsTranslated, l.ad_client_id, l.ad_org_id, "
					+ "		l.Created,l.Createdby,l.Updated,l.UpdatedBy "
					+ "	from ad_Element_trl l, ad_element e, ad_column c "
					+ "	where c.ad_column_id = "
					+ AD_Column_ID
					+ "		and c.ad_element_id = e.ad_element_id "
					+ "		and e.ad_element_id = l.ad_element_id "
					+ "		and l.ad_language not in (select trl.ad_language from ad_field_trl trl, ad_field fi "
					+ "						where trl.ad_field_id = fi.ad_field_id "
					+ "							and fi.ad_field_id =" + AD_Field_ID + ")";

			pstmt = DB.prepareStatement(sqlInsert, get_TrxName());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				X_AD_Field_Trl trl = new X_AD_Field_Trl(getCtx(), 0,
						get_TrxName());
				trl.setAD_Language(rs.getString("ad_language"));
				trl.setAD_Field_ID(AD_Field_ID);
				trl.setDescription(rs.getString("description"));
				trl.setHelp(rs.getString("Help"));
				trl.setName(rs.getString("name"));
				trl.setIsTranslated("Y".equalsIgnoreCase(rs
						.getString("IsTranslated")));
				if (!trl.save())
					log.log(Level.WARNING, "Field_Trl not exported!");
			}

			rs.close();
			pstmt.close();
			pstmt = null;

		} catch (Exception e) {
			log.log(Level.SEVERE, "exportElement2FieldTranslations", e);
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = null;
		} catch (Exception e) {
			pstmt = null;
		}

		return true;
	}

	/** Sobrecarga para compatibilidad con resto de invocaciones */
	public static int getID(String tableName) {
		return getID(tableName, null);
	}
	
	/** Sobrecarga para compatibilidad con PluginXMLUpdater (requiere trxName) */
	public static int getID(String tableName, String trxName) {
		int id = 0;
		String sql = "select AD_TABLE_ID from AD_TABLE where upper(tablename) = upper(?)";
		try {
			PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
			pstmt.setString(1, tableName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				id = rs.getInt(1);
			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Busca los registros de la tabla con nombre tableName que cumplen con las
	 * condiciones dadas en la cláusula where parámetro. Esta cláusula puede
	 * tener parámetros (notación ?) que luego serán cargados al Prepared
	 * Statement. Estos parámetros (whereParams) que llevará el where deben
	 * pasarse en el orden de aparición en la cláusula. Por ejemplo, el primer
	 * parámetro dentro de la cláusula, debe ser el primer elemento dentro del
	 * array de objetos whereParams, y así sucesivamente.
	 * <strong>NOTAS:</strong> No agregar WHERE a la cláusula parámetro, se
	 * agrega automáticamente.
	 * 
	 * @param ctx
	 *            el contexto se utilizará para crear los PO.
	 * @param tableName
	 *            nombre de la tabla involucrada.
	 * @param whereClause
	 *            cláusula where, en el caso de colocar algunas condiciones.
	 * @param whereParams
	 *            parámetros de la cláusula where, en el caso que hubiere.
	 * @param orderByColumns
	 *            nombre de las columnas por las cuales ordenar la búsqueda
	 *            resultante.
	 * @param trxName
	 *            nombre de la transacción actual.
	 * @return Retorna la lista de PO relacionados con la tabla de los registros
	 *         encontrados, lista vacía en el caso de no encontrar registros.
	 * @author Matías Cap - Disytel versión 1.0
	 */
	
	public static List<PO> find(Properties ctx, String tableName,
			String whereClause, Object[] whereParams, String[] orderByColumns,
			String trxName) {
		return find(ctx,tableName,whereClause,whereParams,orderByColumns,trxName,false);
	}
	
	 /** Sobrecarga con parametro noConvert para evitar intentos de conversion de sentencias */
	public static List<PO> find(Properties ctx, String tableName,
			String whereClause, Object[] whereParams, String[] orderByColumns,
			String trxName,boolean noConvert) {
		M_Table table = M_Table.get(ctx, tableName);
		// Armar la consulta sql
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(tableName);
		// Si hay cláusula where, entonces coloco el where parámetro
		if ((whereClause != null) && (whereClause.trim().length() > 0)) {
			sql.append(" WHERE ");
			sql.append(whereClause);
		}
		// Si hay columnas por las cuales ordenar la consulta,
		// entonces coloco el order by
		if ((orderByColumns != null) && (orderByColumns.length > 0)) {
			sql.append(" ORDER BY ");
			for (int i = 0; i < orderByColumns.length; i++) {
				if (orderByColumns[i].trim().length() > 0) {
					sql.append(orderByColumns[i]);
					sql.append(",");
				}
			}
			// Elimino la última coma agregada a las columnas
			sql = sql.deleteCharAt(sql.length() - 1);
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<PO> list = new ArrayList<PO>();
		try {
			ps = DB.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					trxName,noConvert);
			if (whereParams != null) {
				int p = 1;
				for (int i = 0; i < whereParams.length; i++) {
					ps.setObject(p++, whereParams[i]);
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(table.getPO(rs, trxName));
			}
		} catch (Exception e) {
			s_log.severe("ERROR finding from table " + tableName);
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				s_log.severe("ERROR finding from table " + tableName);
				e.printStackTrace();
			}
		}

		return list;
	}

	/**
	 * Obtiene el primer registro de la tabla con nombre tableName que cumplen
	 * con las condiciones dadas en la cláusula where parámetro. Esta cláusula
	 * puede tener parámetros (notación ?) que luego serán cargados al Prepared
	 * Statement. Estos parámetros (whereParams) que llevará el where deben
	 * pasarse en el orden de aparición en la cláusula. Por ejemplo, el primer
	 * parámetro dentro de la cláusula, debe ser el primer elemento dentro del
	 * array de objetos whereParams, y así sucesivamente. <strong>NOTA:</strong>
	 * No agregar WHERE a la cláusula parámetro, se agrega automáticamente.
	 * 
	 * @param ctx
	 *            contexto utilizado para la creación del PO.
	 * @param tableName
	 *            nombre de la tabla utilizada.
	 * @param whereClause
	 *            cláusula where, en el caso de haber condiciones.
	 * @param whereParams
	 *            parámetros de la cláusula where, en el caso que hubiere.
	 * @param orderByColumns
	 *            nombre de las columnas por las cuales ordenar la búsqueda
	 *            resultante.
	 * @param trxName
	 *            nombre de la transacción actual.
	 * @return la primer tupla encontrada de la tabla y las condiciones
	 *         indicadas como parámetro, null en caso de no existir resultados.
	 *         versión 1.0
	 */
	public static PO findFirst(Properties ctx, String tableName,
			String whereClause, Object[] whereParams, String[] orderByColumns,
			String trxName) {
		PO po = null;
		// Busco los registros a partir de los datos parámetro
		List<PO> findList = PO.find(ctx, tableName, whereClause, whereParams,
				orderByColumns, trxName);
		// Si se encontraron registros, agarro el primero de ellos
		if (findList.size() > 0) {
			po = findList.get(0);
		}
		return po;
	}

	/**
	 * Obtiene el primer registro de la tabla con nombre tableName que cumplen
	 * con las condiciones dadas en la cláusula where parámetro. Esta cláusula
	 * puede tener parámetros (notación ?) que luego serán cargados al Prepared
	 * Statement. Estos parámetros (whereParams) que llevará el where deben
	 * pasarse en el orden de aparición en la cláusula. Por ejemplo, el primer
	 * parámetro dentro de la cláusula, debe ser el primer elemento dentro del
	 * array de objetos whereParams, y así sucesivamente. <strong>NOTA:</strong>
	 * No agregar WHERE a la cláusula parámetro, se agrega automáticamente.
	 * 
	 * @param ctx
	 *            contexto utilizado para la creación del PO.
	 * @param tableName
	 *            nombre de la tabla utilizada.
	 * @param whereClause
	 *            cláusula where, en el caso de haber condiciones.
	 * @param whereParams
	 *            parámetros de la cláusula where, en el caso que hubiere.
	 * @param orderByColumns
	 *            nombre de las columnas por las cuales ordenar la búsqueda
	 *            resultante.
	 * @param noConvert
	 *            no convierte el sql
	 * @param trxName
	 *            nombre de la transacción actual.
	 * @return la primer tupla encontrada de la tabla y las condiciones
	 *         indicadas como parámetro, null en caso de no existir resultados.
	 *         versión 1.0
	 */
	public static PO findFirst(Properties ctx, String tableName,
			String whereClause, Object[] whereParams, String[] orderByColumns,
			String trxName, boolean noConvert) {
		PO po = null;
		// Busco los registros a partir de los datos parámetro
		List<PO> findList = PO.find(ctx, tableName, whereClause, whereParams,
				orderByColumns, trxName, noConvert);
		// Si se encontraron registros, agarro el primero de ellos
		if (findList.size() > 0) {
			po = findList.get(0);
		}
		return po;
	}
	
	/**
	 * @return Returns the docActionStatusListeners.
	 */
	public List<DocActionStatusListener> getDocActionStatusListeners() {
		return m_docActionStatusListeners;
	}

	/**
	 * Adds a <code>DocActionStatusListener</code> to the list of listeners.
	 * 
	 * @param listener
	 *            <code>DocActionStatusListener</code> to add.
	 */
	public void addDocActionStatusListener(DocActionStatusListener listener) {
		if (!getDocActionStatusListeners().contains(listener))
			getDocActionStatusListeners().add(listener);
	}

	/**
	 * Removes a <code>DocActionStatusListener</code> from the list of
	 * listeners.
	 * 
	 * @param listener
	 *            <code>DocActionStatusListener</code> to remove;
	 */
	public void removeDocActionStatusListener(DocActionStatusListener listener) {
		getDocActionStatusListeners().remove(listener);
	}

	/**
	 * Fires a DocAction status changed event.
	 * 
	 * @param event
	 *            The event to fire.
	 */
	protected void fireDocActionStatusChanged(DocActionStatusEvent event) {
		for (DocActionStatusListener listener : getDocActionStatusListeners()) {
			listener.docActionStatusChanged(event);
		}
	}

	/**
	 * Adds all of the DocAction Status Listeners having <code>fromPO</code> to
	 * <code>this</code> PO.
	 * 
	 * @param fromPO
	 *            The PO containing the listeners.
	 */
	protected void copyDocActionStatusListeners(PO fromPO) {
		for (DocActionStatusListener listener : fromPO
				.getDocActionStatusListeners()) {
			addDocActionStatusListener(listener);
		}
	}

	/**
	 * Ampliación para lógica de negocios con plugins Por cada evento, por
	 * ejemplo beforeSave, se disparan los correspondientes métodos
	 * preBeforeSave y postBeforeSave
	 * 
	 * @return
	 */
	private boolean handlePersistence(boolean newRecord, boolean success,
			PluginPOHandler handler) {
		// Cargar listado de plugins que deben ejecutarse
		Vector<MPluginPO> pluginList = PluginPOUtils.getPluginList(this);

		// Invocar el procesamiento segun la lista de plugins
		return handler.processPO(this, newRecord, success, pluginList, log);
	}

	/** Adapter por limitacion "protected" de metodo beforeSave() */
	public boolean doBeforeSave(boolean newRecord) {
		return beforeSave(newRecord);
	}

	/** Adapter por limitacion "protected" de metodo afterSave() */
	public boolean doAfterSave(boolean newRecord, boolean success) {
		return afterSave(newRecord, success);
	}

	/** Adapter por limitacion "protected" de metodo beforeDelete() */
	public boolean doBeforeDelete() {
		return beforeDelete();
	}

	/** Adapter por limitacion "protected" de metodo afterDelete() */
	public boolean doAfterDelete(boolean success) {
		return afterDelete(success);
	}

	/** Getter para implementar DocAction */
	public String getProcessMsg() {
		return m_processMsg;
	} // getProcessMsg

	/** Setter para permitir indicar en plugins */
	public void setProcessMsg(String aMessage) {
		m_processMsg = aMessage;
	}
	
	/** Getter para implementar DocAction */
	public String getSummary() {
		return summary;
	}

	/** Setter para permitir indicar en plugins */
	public void setSummary(String aSummary) {
		summary = aSummary;
	}

	/**
	 * Confecciona el UID a partir de los elementos parámetro, separados por el
	 * separator
	 * 
	 * @param elementsUID
	 *            array de strings elementos del UID
	 * @return UID
	 */
	public static String makeUID(List<String> elementsUID) {
		StringBuffer uid = new StringBuffer();
		for (String element : elementsUID) {
			uid.append(element).append(SEPARATORUID);
		}
		String realUID = uid.toString();
		if (uid.length() > 0) {
			realUID = uid.substring(0, uid.lastIndexOf(SEPARATORUID));
		}
		return realUID;
	}

	/**
	 * Crea el valor y lo setea al campo AD_ComponentObjectUID, si es que éste
	 * existe dentro de la tabla.
	 */
	protected boolean makeAndSetComponentObjectUID() {
		boolean ok = true;
		int indexUID = p_info.getColumnIndexIgnoreCase("AD_ComponentObjectUID");
		MComponentVersion componentVersion = MComponentVersion
				.getCurrentComponentVersion(getCtx(), get_TrxName());
		// Si existe la columna AD_ComponentObjectUID,
		// existe un componente en desarrollo y
		// esa columna no tiene un valor asignado
		// entonces le agrego un valor automáticamente que corresponde con
		// paqueteDePlugin_tablename_recordID
		if ((indexUID > -1)
				&& ((m_newValues[indexUID] == null) || (m_newValues[indexUID] == NULL))
				&& (componentVersion != null)) {
			MComponent component = new MComponent(getCtx(), componentVersion
					.getAD_Component_ID(), get_TrxName());
			List<String> list = new ArrayList<String>();
			list.add(component.getPrefix());
			list.add(get_TableName());
			for (int i = 0; i < m_KeyColumns.length; i++) {
				list.add(String.valueOf(get_Value(m_KeyColumns[i])));
			}
			set_Value(indexUID, makeUID(list));
		}
		return ok;
	}

	/**
	 * Logging en el change log.
	 * 
	 * @param operationType
	 *            tipo de operación I, M o D.
	 * @return true si se agregó con éxito el log de las modificaciones.
	 */
	protected boolean logging(String operationType) {
		boolean ok = true;
		try {

			// Save change log - Used for components developement
			MComponentVersion componentVersion = MComponentVersion
					.getCurrentComponentVersion(getCtx(), get_TrxName());
			if ((shouldAudit(p_info.getTableName(), operationType)) || (MChangeLog.isLogged(p_info.getAD_Table_ID())
					&& passTableExceptions(operationType)
					&& componentVersion != null)) {
				// Change Log
				MSession session = MSession.get(p_ctx, false);
				if (session == null) {
					log.fine("No Session found");
				}
				// AD_ComponentVersion
				Integer valueVersion = (componentVersion!=null?componentVersion.getID():0);
				if (p_info.getTableName().equalsIgnoreCase("AD_Component")
						|| p_info.getTableName().equalsIgnoreCase(
								"AD_ComponentVersion")) {
					valueVersion = 0;
				}
				// Get index of colum name AD_ComponentObjectUID
				int indexUID = p_info
						.getColumnIndexIgnoreCase("AD_ComponentObjectUID");
				boolean existUID = indexUID >= 0;
				// Get value of column AD_ComponentObjectUID
				Object valueUID = null;
				if (existUID) {
					valueUID = m_oldValues[indexUID];
					if (valueUID != null && valueUID == NULL) {
						valueUID = null;
					}
					if (valueUID == null) {
						valueUID = m_newValues[indexUID];
						if (valueUID != null && valueUID == NULL) {
							valueUID = null;
						}
					}
				}
				MChangeLog cLog;
				Object newV;
				Object oldV;
				Object value;
				if (((m_IDs.length == 1) || existUID) && session != null) {
					Integer changeLogGroupID = null;
					if (!operationType
							.equals(MChangeLog.OPERATIONTYPE_Deletion)) {
						int size = get_ColumnCount();
						for (int i = 0; i < size; i++) {
							value = m_newValues[i];
							oldV = m_oldValues[i];
							newV = m_newValues[i];
							if (value != null
									&& passColumnExceptions(
											p_info.getColumn(i), operationType)) {
								// Si no está definido el group id con
								// getNexSequenceID
								// lo llamo
								if (changeLogGroupID == null) {
									changeLogGroupID = MSequence
											.getNextSequenceID(
													getAD_Client_ID(),
													"seq_ad_changelog_group",
													get_TrxName());
								}
								if (oldV != null && oldV == NULL)
									oldV = null;
								if (newV != null && newV == NULL)
									newV = null;
								cLog = session.changeLog(m_trxName, 0, p_info
										.getAD_Table_ID(),
										p_info.getColumn(i).AD_Column_ID,
										getID(), getAD_Client_ID(),
										getAD_Org_ID(), oldV, newV,
										(String) valueUID, valueVersion,
										operationType,
										p_info.getColumn(i).DisplayType,
										changeLogGroupID);
							}
						}
					} else {
						// Si no está definido el group id con getNexSequenceID
						// lo llamo
						if (changeLogGroupID == null) {
							changeLogGroupID = MSequence.getNextSequenceID(
									getAD_Client_ID(),
									"seq_ad_changelog_group", get_TrxName());
						}
						oldV = null;
						newV = null;
						cLog = session.changeLog(m_trxName, 0, p_info
								.getAD_Table_ID(), p_info
								.getAD_Column_ID(m_KeyColumns[0]), getID(),
								getAD_Client_ID(), getAD_Org_ID(), oldV, newV,
								(String) valueUID, valueVersion, operationType,
								DisplayType.Integer, changeLogGroupID);
					}
				}
			}
		} catch (Exception e) {
			ok = false;
			log.severe("No se pudo registrar el log de cambios para la tabla "
					+ p_info.getTableName());
		}
		return ok;
	}

	/**
	 * @return true pasa las excepciones de registros de tablas de logueo, false
	 *         si hay excepciones a loguear
	 */
	private boolean passTableExceptions(String operationType) {
		boolean ok = true;
		// Si la tabla es ad_sequence, loggear únicamente los documentno
		if (get_TableName().equalsIgnoreCase("AD_Sequence")) {
			ok = !MSequence.isTableSequence(getCtx(), getID(), get_TrxName());
		}
		return ok;
	}

	/**
	 * @return true pasa las excepciones de columnas de logueo, false si hay
	 *         excepciones a loguear
	 */
	private boolean passColumnExceptions(POInfoColumn infoColumn,
			String operationType) {
		boolean ok = true;
		// Si estamos modificando
		if (operationType.equals(MChangeLog.OPERATIONTYPE_Modification)) {
			// filtrar nombres de columna
			ok = !infoColumn.ColumnName.equalsIgnoreCase("Processing")
					&& !infoColumn.ColumnName
							.equalsIgnoreCase("Statistic_Seconds")
					&& !infoColumn.ColumnName
							.equalsIgnoreCase("Statistic_Count")
					// Se comenta esta línea ya que no existe motivo por el cual
					// no bitacorear esta columna. Los nuevos componentes que
					// deseen bitacorear entradas no podrán desarrollar sus
					// componentes en una instalación del mismo en otra BD
//					&& !infoColumn.ColumnName.equalsIgnoreCase("IsChangeLog")
					// Columna de datos binarios
					&& infoColumn.DisplayType != DisplayType.Binary
					&& !infoColumn.ColumnName
							.equalsIgnoreCase("CurrentDevelopment")
					&& !infoColumn.ColumnName
							.equalsIgnoreCase("StartDevelopment")
					&& !infoColumn.ColumnName.equalsIgnoreCase("DateLastRun");
		}
		return ok;
	}

	/**
	 * Copia traducciones de una tabla a la otra.
	 * 
	 * @param from
	 */
	public void copyTranslation(PO from) {
		// Si no son de las mismas tablas, error
		if (!get_TableName().equalsIgnoreCase(from.get_TableName())) {
			log
					.severe("Error al copiar traducciones: No se puede copiar traducciones de tablas diferentes");
			return;
		}
		// Busco las traducciones a copiar
		StringBuffer sql = new StringBuffer("SELECT * FROM ");
		sql.append(get_TableName()).append("_trl ");
		sql.append("WHERE ");
		sql.append(get_TableName()).append("_id = ?");
		// Where clause
		StringBuffer whereClause = new StringBuffer();
		whereClause.append(get_TableName()).append("_id = ? ").append(
				" AND ad_language = ?");

		PreparedStatement ps = null;
		PreparedStatement psTo = null;
		ResultSet rs = null;
		ResultSet rsTo = null;
		M_Table table = M_Table.get(getCtx(), get_TableName() + "_trl");
		PO toTrl = null;
		PO fromTrl = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			ps.setInt(1, from.getID());
			rs = ps.executeQuery();
			// Me guardo las traducciones del PO from
			while (rs.next()) {
				// From trl de ese id y lenguaje
				fromTrl = table.getPO(rs, get_TrxName());
				// Busco el to trl de mi id y el lenguaje
				toTrl = PO.findFirst(getCtx(), get_TableName() + "_trl",
						whereClause.toString(), new Object[] { getID(),
								rs.getString("ad_language") }, null,
						get_TrxName());
				// Si no existe el registro trl para ese lenguaje
				if (toTrl == null) {
					toTrl = table.getPO(0, get_TrxName());
				}
				// Copio los valores
				PO.copyValues(fromTrl, toTrl);
				// Seteo el id con mi id, sino queda con el id del from trl
				toTrl.set_Value(get_TableName() + "_ID", getID());
				// Guardo
				if (!toTrl.save()) {
					throw new Exception("Error al guardar la traduccion de "
							+ get_TableName());
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "copy Translation - " + sql, e);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (psTo != null)
					psTo.close();
				if (rs != null)
					rs.close();
				if (rsTo != null)
					rsTo.close();
			} catch (Exception e) {
				log.log(Level.SEVERE, "copy Translation - " + sql, e);
			}
		}
	}

	/**
	 * Copia registros template de la tabla parámetro, obteniendolos con la
	 * cláusula where parámetro y los valores de esa cláusula where. Las
	 * columnas customs son columnas a actualizar para cada tabla en particular,
	 * los valores de ellas se encuentran en el parámetro customColumnsValues.<br>
	 * <strong>CUIDADO</strong>: Para tablas con claves múltiples la map
	 * resultado no es consistente ya que el método getID() de PO devuelve 0 en
	 * esos casos.
	 * 
	 * @param ctx
	 *            contexto
	 * @param tableName
	 *            nombre de la tabla a copiar registros
	 * @param whereClause
	 *            claúsula where para obtener los registros a copiar de la tabla
	 *            parámetro (registros template)
	 * @param whereClauseParams
	 *            parámetros a insertar en los ? de la cláusula where (deben
	 *            estar en orden)
	 * @param customColumnsNames
	 *            nombre de las columnas a actualizar para esta tabla en
	 *            particular
	 * @param customColumnsValues
	 *            valores de las columnas a actualizar para esta tabla en
	 *            particular
	 * @param trxName
	 *            nombre de transacción en curso
	 * @return map siendo:
	 *         <ul>
	 *         <li>Clave = ids de los registros template</li>
	 *         <li>Valor = ids de los nuevos registros creados a partir de los
	 *         template</li>
	 *         </ul>
	 *         Cuidado con claves múltiples!!
	 * @throws Exception
	 */
	public static Map<Integer, Integer> copyFrom(Properties ctx,
			String tableName, String whereClause, Object[] whereClauseParams,
			String[] customColumnsNames, Object[] customColumnsValues,
			String trxName) throws Exception {
		return copyFrom(ctx, tableName, whereClause, whereClauseParams,
				customColumnsNames, customColumnsValues, false, null, trxName);
	}

	/**
	 * Copia registros template de la tabla parámetro, obteniendolos con la
	 * cláusula where parámetro y los valores de esa cláusula where. Las
	 * columnas customs son columnas a actualizar para cada tabla en particular,
	 * los valores de ellas se encuentran en el parámetro customColumnsValues.
	 * Por ejemplo, supongamos que deseamos copiar las cuentas bancarias de una
	 * entidad comercial template a otra, entonces lo que se hace es buscar las
	 * cuentas bancarias de la entidad comercial template (se supone dentro del
	 * where irá la cláusula c_bpartner_id = ?, con su respectivo parámetro) y
	 * en las columnas custom a modificar de las nuevas cuentas bancarias se
	 * debe setear el id de la entidad comercial nueva, entonces en las columnas
	 * custom debe estar C_BPartner_ID (respetando mayúsculas como está indicado
	 * en metadatos) y el valor custom debería ser la nueva entidad comercial
	 * que desea tener las mismas cuentas bancarias que la entidad comercial
	 * template.<br>
	 * <strong>CUIDADO</strong>: Para tablas con claves múltiples la map
	 * resultado no es consistente ya que el método getID() de PO devuelve 0 en
	 * esos casos. Para la creación de nuevos PO se puede utilizar el parámetro
	 * customColumnTemplateValue que determina si los valores null pasados a las
	 * columnas custom de esta tabla particular toman el valor del PO template.
	 * Nos puede pasar que para algunos casos los valores null pasados deben
	 * setearse efectivamente a la columna custom y otros tomar el valor del PO
	 * template, para estos casos debemos utilizar el parámetro
	 * customColumnTemplateValueTimes que determina la cantidad de veces que
	 * debemos tomar el valor del template. Por ejemplo, si nosostros le pasamos
	 * 3 nombre de columnas custom y 3 valores null para estas columnas y
	 * queremos que se seteen los primeros 2 del valor del template y el
	 * restante que efectivamente sea null, le podemos pasar al parámetro de
	 * cantidad 2 lo que significa que se deben tomar los 2 primeros del
	 * template y el restante no.
	 * 
	 * @param ctx
	 *            contexto
	 * @param tableName
	 *            nombre de la tabla a copiar registros
	 * @param whereClause
	 *            claúsula where para obtener los registros a copiar de la tabla
	 *            parámetro (registros template)
	 * @param whereClauseParams
	 *            parámetros a insertar en los ? de la cláusula where (deben
	 *            estar en orden)
	 * @param customColumnsNames
	 *            nombre de las columnas a actualizar para esta tabla en
	 *            particular
	 * @param customColumnsValues
	 *            valores de las columnas a actualizar para esta tabla en
	 *            particular
	 * @param customColumnTemplateValue
	 *            true si los valores null de las columnas custom deben tomar
	 *            los valores del template con el nombre de columna parámetro
	 * @param customColumnTemplateValueTimes
	 *            cantidad de veces que se deben setear los valores del PO
	 *            template para la columna en orden si el valor pasado es null
	 * @param trxName
	 *            nombre de transacción en curso
	 * @return map siendo:
	 *         <ul>
	 *         <li>Clave = ids de los registros template</li>
	 *         <li>Valor = ids de los nuevos registros creados a partir de los
	 *         template</li>
	 *         </ul>
	 *         Cuidado con claves múltiples!!
	 * @throws Exception
	 *             si existe un error
	 */
	public static Map<Integer, Integer> copyFrom(Properties ctx,
			String tableName, String whereClause, Object[] whereClauseParams,
			String[] customColumnsNames, Object[] customColumnsValues,
			boolean customColumnTemplateValue,
			Integer customColumnTemplateValueTimes, String trxName)
			throws Exception {
		// Obtengo los registros de la tabla parámetro de la entidad comercial
		// template
		List<PO> pos = PO.find(ctx, tableName, whereClause, whereClauseParams,
				null, trxName);
		// Obtengo la clase a instanciar a partir del nombre de la tabla
		Class<?> classToCopy = M_Table.getClass(tableName);
		// Inicializo la map que asocia los registros antiguos con los nuevos
		// para esta tabla
		Map<Integer, Integer> newRecords = new HashMap<Integer, Integer>();
		// Itero por los registros template de la tabla parámetro encontrados y
		// creo los nuevos
		for (PO templatePO : pos) {
			// Obtener el constructor que identifica un nuevo registro de la
			// clase a copiar
			Constructor<?> newConstructor = classToCopy
					.getConstructor(new Class<?>[] { Properties.class,
							int.class, String.class });
			// Crear una nueva instancia
			PO newPO = (PO) newConstructor.newInstance(new Object[] { ctx, 0,
					trxName });
			// Copiar los valores
			PO.copyValues(templatePO, newPO);
			// Realizar la asignación de las columnas custom para esta tabla
			// particular
			if (customColumnsNames != null) {
				// Itero por los nombres de las columnas y les asigno los
				// valores correspondientes
				Object value;
				for (int i = 0; i < customColumnsNames.length; i++) {
					value = customColumnsValues[i];
					// Si el valor pasado es null entonces verifico la cantidad
					// de veces del seteo de valores del PO template
					if (value == null) {
						if (customColumnTemplateValue
								&& customColumnTemplateValueTimes != null
								&& customColumnTemplateValueTimes > 0) {
							value = templatePO.get_Value(customColumnsNames[i]);
							customColumnTemplateValueTimes -= 1;
						}
					}
					newPO.set_ValueNoCheck(customColumnsNames[i], value);
				}
			}
			// Guardar el nuevo registro
			Method methodSave = classToCopy
					.getMethod("save", new Class<?>[] {});
			// Invoco al método save del nuevo PO
			Boolean saved = (Boolean) methodSave.invoke(newPO, new Object[] {});
			// Si no fue guardado, entonces tiro una excepción
			if (!saved) {
				String srcMsg = CLogger.retrieveErrorAsString();
				throw new Exception(
						"Error al guardar un nuevo registro de la tabla "
								+ tableName
								+ ", copia del registro plantilla con ID "
								+ templatePO.getID()
								+ (Util.isEmpty(srcMsg) ? ""
										: ". Error fuente: " + srcMsg));
			}
			// Guardar en la map creada
			newRecords.put(templatePO.getID(), newPO.getID());
		}
		return newRecords;
	}

	/**
	 * @param ctx
	 *            contexto
	 * @param tableName
	 *            nombre de tabla
	 * @param whereClause
	 *            cláusula where
	 * @param whereParams
	 *            parámetros del where
	 * @param trxName
	 *            nombre de la transacción
	 * @return true si existe al menos un registro en esa tabla y esa cláusula
	 *         where, false si no existen registros de esa tabla con esos
	 *         criterios
	 */
	public static boolean existRecordFor(Properties ctx, String tableName, String whereClause, Object[] whereParams, String trxName){
		// Armar la consulta sql
		StringBuffer sql = new StringBuffer("SELECT coalesce(count(1),0)::integer as cant FROM ").append(tableName);
		// Si hay cláusula where, entonces coloco el where parámetro
		if ((whereClause != null) && (whereClause.trim().length() > 0)) {
			sql.append(" WHERE ");
			sql.append(whereClause);
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		int noRecords = 0;
		try{
			ps = DB.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					trxName);
			if (whereParams != null) {
				int p = 1;
				for (int i = 0; i < whereParams.length; i++) {
					ps.setObject(p++, whereParams[i]);
				}
			}
			rs = ps.executeQuery();
			if(rs.next()){
				noRecords = rs.getInt("cant");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return noRecords > 0;
	}

	/**
	 * Determina si existe la columna en la tabla
	 * @param ctx
	 * @param tableName
	 * @param columnName
	 * @param trxName
	 * @param throwIfFalse
	 * @return
	 * @throws Exception
	 */
	public static boolean existsColumnInTable(Properties ctx, String tableName, String columnName, String trxName, boolean throwIfFalse) throws Exception{
		Integer count = DB
				.getSQLValue(
						trxName,
						"SELECT coalesce(count(*),0)::integer FROM information_schema.columns WHERE upper(trim(table_name)) = upper(trim('"
								+ tableName
								+ "')) AND upper(trim(column_name)) = upper(trim('"
								+ columnName + "'))");
		boolean exists = count >= 1;
		if(!exists && throwIfFalse){
			throw new Exception(Msg.getMsg(ctx, "NotExistsColumnInTable",
					new Object[] { columnName, tableName }));
		}
		return exists;
	}
	
	/**
	 * Verifica si el usuario parámetro tiene acceso al componente de la tabla
	 * parámetro. Recordar que para el acceso a pestaña o a campo, la existencia
	 * del registro significa que no posee acceso o un acceso restringido.
	 * 
	 * @param ctx
	 *            contexto
	 * @param userID
	 *            id de usuario
	 * @param accessTableName
	 *            nombre de tabla de acceso
	 * @param aditionalWhereClause
	 *            cláusula where adicional en caso que se requiera. NOTA: La
	 *            cláusula debe tener como prefijo un AND.
	 * @param accessKeyColumnName
	 *            nombre de la columna clave de acceso, por ejemplo si la tabla
	 *            es AD_Process_Para, la columna clave sería ad_process_id
	 * @param accessKeyColumnValue
	 *            valor del nombre de la columna de clave de acceso
	 * @param trxName
	 *            transacción actual
	 * @return true si posee acceso, false caso contrario o posee acceso
	 *         restringido
	 */
	public static boolean hasAccessToComponent(Properties ctx, Integer userID, String accessTableName, String aditionalWhereClause, String accessKeyColumnName, Integer accessKeyColumnValue, String trxName){
		Integer accessResult = null;
		boolean hasAccess = true;
		String sql = "SELECT a."
				+ accessKeyColumnName
				+ " FROM "
				+ accessTableName
				+ " as a INNER JOIN ad_user_roles as ur ON ur.ad_role_id = a.ad_role_id WHERE a."
				+ accessKeyColumnName
				+ " = ? AND ur.ad_user_id = ? AND ur.isactive = 'Y' AND a.isactive = 'Y' "
				+ (Util.isEmpty(aditionalWhereClause, true) ? ""
						: aditionalWhereClause);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			int i = 1;
			ps.setInt(i++, accessKeyColumnValue);
			ps.setInt(i++, userID);
			rs = ps.executeQuery();
			if(rs.next()){
				accessResult = rs.getInt(accessKeyColumnName);
			}
			// Si no hubo resultado entonces hay acceso
			if(Util.isEmpty(accessResult, true)){
				hasAccess = false;
			}
			else{
				// Si la tabla es acceso a pestaña o a campo, la existencia de un
				// registro significa que no posee acceso
				if (accessTableName
						.equalsIgnoreCase(X_AD_Tab_Access.Table_Name)
						|| accessTableName
								.equalsIgnoreCase(X_AD_Field_Access.Table_Name)) {
					hasAccess = false;
				}
			}
		} catch (Exception e) {
			s_log.severe("ERROR finding user access in table "
					+ accessTableName + " with access key column name "
					+ accessKeyColumnName + " its value "
					+ accessKeyColumnValue + " and user id " + userID
					+ ". SQL builded = " + sql);
			e.printStackTrace();
		} finally{
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e2) {
				s_log.severe("ERROR finding user access in table "
						+ accessTableName + " with access key column name "
						+ accessKeyColumnName + " its value "
						+ accessKeyColumnValue + " and user id " + userID
						+ ". SQL builded = " + sql);
				e2.printStackTrace();
			}
		}
		return hasAccess;
	}

	/**
	 * Método implementado por las subclases necesarias
	 * 
	 * @param auxDTO
	 *            auxiliar
	 * @param processed
	 *            determina si ya se procesó el documento o estamos en eso
	 */
	public void setAuxiliarInfo(AuxiliarDTO auxDTO, boolean processed){
		
	}
	/**
	 * Verifica si la cabecera de una línea se encuentra en modo borrador
	 * - Sugerencia de Javier Ader
	 * @param headerTable nombre de la tabla cabecera
	 * @param headerRecordID id del registro cabecera 
	 * @return verdadero en caso que la cabecera se encuentra en modo borrador, falso en caso contrario
	 */
    public boolean isHeaderUpdateable(String headerTable, int headerRecordID)
    {
        String sql = "SELECT DocStatus FROM " + headerTable + " WHERE " + headerTable+"_ID = ?"; 
        String docStatusHeader = DB.getSQLValueString(get_TrxName(), sql, headerRecordID);
        
        return DocAction.STATUS_Drafted.equals(docStatusHeader) ||
                DocAction.STATUS_InProgress.equals(docStatusHeader) ||
                DocAction.STATUS_WaitingConfirmation.equals(docStatusHeader);
        
    }

	public boolean isDirectInsert() {
		return directInsert;
	}

	public void setDirectInsert(boolean directInsert) {
		this.directInsert = directInsert;
	}
	
	public boolean isSkipHandlers() {
		return skipHandlers;
	}

	public void setSkipHandlers(boolean skipHandlers) {
		this.skipHandlers = skipHandlers;
	}


	public void setLog(CLogger log){
		this.log = log;
	}

	public CLogger getLog(){
		if(this.log == null){
			setLog(CLogger.getCLogger(getClass()));
		}
		return this.log;
	}

	/**
	 * Verifica si existe un valor string duplicado para la columna y tabla
	 * parámetro
	 * 
	 * @param tableName
	 *            nombre de la tabla
	 * @param columnName
	 *            nombre de la columna a verificar
	 * @param columnKey
	 *            nombre de la columna clave de la tabla. Esto también sirve
	 *            para obtener una clave del mensaje de error en el caso que
	 *            existan duplicados y deba guardarse el error.
	 * @param value
	 *            valor de la columna, si es null no tiene caso la validación
	 * @param newRecord
	 *            true si es un registro nuevo, false caso contrario. Esto
	 *            permite también validar que no sea el registro actual
	 * @param saveError
	 *            true si se debe guardar el error en el log, mediante la
	 *            sentencia saveError()
	 * @return true si existen duplicados para esa columna en esa tabla, false
	 *         si no hay duplicados
	 */
	protected boolean sameColumnValueValidation(String tableName, String columnName, String columnKey, String value, boolean newRecord, boolean saveError){
		if(value != null){
			// Validación de campo Value duplicado: no se permiten EC con el mismo código.
			String sql = " SELECT COALESCE(COUNT("+columnKey+"),0) " +
					     " FROM " + tableName +
					     " WHERE trim("+columnName+") = trim('"+value+"') " +
					     "	 AND AD_Client_ID = ?";
			if(!newRecord){
				sql += "   AND "+columnKey+" <> " + getID();
			}
			Long existValue = (Long) DB.getSQLObject(get_TrxName(), sql,
					new Object[] { getAD_Client_ID() }); 
			if (existValue > 0) {
				// Guardo el error en caso que se requiera
				if(saveError){
					log.saveError("SaveError", getDuplicatedFieldValueMsg("Value", columnKey, value.trim()));
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Mensaje de duplicación de de valor de la columna
	 * 
	 * @param fieldName
	 *            nombre de la columna de duplicación
	 * @param fieldMsgKey
	 *            nombre de la columna clave para el mensaje
	 * @param fieldValue
	 *            valor de duplicación
	 * @return el mensaje de duplicación con el mapeo de los parámetros
	 *         correspondientes
	 */
	protected String getDuplicatedFieldValueMsg(String fieldName, String fieldMsgKey, String fieldValue) {
		return Msg.parseTranslation(getCtx(), 
			Msg.getMsg(getCtx(), "DuplicatedFieldValue", new Object[] {
					"@" + fieldMsgKey + "@",
					fieldValue,
					"@" + fieldName + "@"
			})
		);
	}

	public void setFromTab(boolean isFromTab) {
		this.isFromTab = isFromTab;
	}

	public boolean isFromTab() {
		return isFromTab;
	}
	
	/**
	 *  Get Table ID.
	 *  @return table id
	 */
	public int get_Table_ID()
	{
		return p_info.getAD_Table_ID();
	}   //  get_TableID
	
	/**
	 * Retorna los nombres de las columnas que están marcadas como isidentifier para este PO
	 */
	public ArrayList<String> getIdentifiersColumnNames() {
		ArrayList<String> identifierColumnNames = new ArrayList<String>();
		try {
			PreparedStatement pstmt = DB.prepareStatement("SELECT columnname FROM AD_Column WHERE AD_Table_ID = " + get_Table_ID() + " AND isidentifier = 'Y'", get_TrxName());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				identifierColumnNames.add(rs.getString("columnname"));
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error al recuperar las columnas identificadoras: " + e.getMessage());
		}
		return identifierColumnNames;
	}
	
	/**
	 * Retorna los valores de las columnas marcadas como isidentifier para este PO
	 */
	public ArrayList<String> getIdentifierValues() {
		ArrayList<String> values = new ArrayList<String>();
		for (String aColumnName : getIdentifiersColumnNames()) {
			values.add(get_ValueAsString(aColumnName));
		}
		return values;
	}
	
	/**
	 * Suma la columna con nombre columnName de la tabla con nombre tableName 
	 * que cumplen con las condiciones dadas en la cláusula where parámetro. 
	 * Esta cláusula puede tener parámetros (notación ?) que luego serán 
	 * cargados al Prepared Statement. Estos parámetros (whereParams) 
	 * que llevará el where deben pasarse en el orden de aparición en la 
	 * cláusula. Por ejemplo, el primer parámetro dentro de la cláusula, 
	 * debe ser el primer elemento dentro del array de objetos whereParams, 
	 * y así sucesivamente.
	 * <strong>NOTAS:</strong> No agregar WHERE a la cláusula parámetro, se
	 * agrega automáticamente.
	 * 
	 * @param ctx
	 *            el contexto se utilizará para crear los PO.
	 * @param tableName
	 *            nombre de la tabla involucrada.
	 * @param columnName
	 *            nombre de la columna involucrada.
	 * @param whereClause
	 *            cláusula where, en el caso de colocar algunas condiciones.
	 * @param whereParams
	 *            parámetros de la cláusula where, en el caso que hubiere.
	 * @param trxName
	 *            nombre de la transacción actual.
	 * @return Retorna la suma de la columna indicada, BigDecimal.ZERO
	 * 			en caso de no se encuentren registros para sumar.
	 * @author Gabriel Hernández - Disytel versión 1.0
	 */
	public static BigDecimal getSumColumn(Properties ctx, String tableName, String columnName,
			String whereClause, Object[] whereParams, String trxName) {
		// Armar la consulta sql
		StringBuffer sql = new StringBuffer("SELECT SUM(").append(columnName).append(") as ").append(columnName).append(" FROM ").append(tableName);
		// Si hay cláusula where, entonces coloco el where parámetro
		if ((whereClause != null) && (whereClause.trim().length() > 0)) {
			sql.append(" WHERE ");
			sql.append(whereClause);
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal res= BigDecimal.ZERO;
		try {
			ps = DB.prepareStatement(sql.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE,
					trxName);
			if (whereParams != null) {
				int p = 1;
				for (int i = 0; i < whereParams.length; i++) {
					ps.setObject(p++, whereParams[i]);
				}
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				res = rs.getBigDecimal(columnName);
			}
		} catch (Exception e) {
			s_log.severe("ERROR finding from table " + tableName);
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				s_log.severe("ERROR finding from table " + tableName);
				e.printStackTrace();
			}
		}

		return res;
	}

	/**
	 * Método que permite copiar valores de instancia de un PO sobre otro.
	 * Utilizado al copiar PO a Helpers de Componentes
	 * 
	 * @param copy el PO a copiar valores
	 */
	public void copyInstanceValues(PO to){
		// Por ahora no se hace nada aca ya que no existen valores de instancia a copiar
	}
	
	/** Cache de referencias */
	protected static CCache<String, Set<String>> ref_cache = new CCache<String, Set<String>>("ref_cache", 50, 10);
	
	/** Dada una refID, obtener la lista de opciones */
	protected static Set<String> getRefCache(String refUID) {
		// Si no existe en la cache, incorporarla
		if (ref_cache.get(refUID) == null) {
			KeyNamePair[] options = DB.getKeyNamePairs("SELECT ad_ref_list_id, value FROM ad_ref_list WHERE ad_reference_id = (SELECT AD_Reference_ID FROM AD_Reference WHERE AD_ComponentObjectUID = '" + refUID + "')", false);
			HashSet<String> aSet = new HashSet<String>();
			for (KeyNamePair option : options) {
				aSet.add(option.getName());
			}
			ref_cache.put(refUID, aSet);
		}
		return ref_cache.get(refUID);
	}
	
	/** Dada una referencia, verificar si la opcion dada es valida */
	public static boolean refContainsValue(String refUID, String value) {
		return getRefCache(refUID).contains(value);
	}
	
	/** Dada una referencia, retornar las opciones validas */
	public static String refValidOptions(String refUID) {
		StringBuffer retValue = new StringBuffer();
		for (String ref : getRefCache(refUID))
			retValue.append(" - ").append(ref).append(" ");
		return retValue.toString();
	}

	/** Nombre de la preferencia con la configuracion de tablas a auditar */
	public static final String AUDIT_EVENTS_CONFIGURATION_PREFERENCE = "AuditEventsConfiguration";
	
	/** Cache de configuracion de auditoria 
	 * 	La configuracion debe realizarse con el siguiente formato: TABLA = ACCIONES_A_AUDITAR : TABLA = ACCIONES_A_AUDITAR : ...
	 * 	Ejemplo: C_OrderLine = IMD : C_Order = DM 
	 *  Siempre debe usarse el signo = para separar tabla y acciones a auditar.
	 *  Siempre debe usarse el signo : para separar entre cada configuración de tabla
	 *  Pueden existir o no separaciones entre las tablas y las acciones. El orden de las acciones es indistinto.  
	 *	Las tablas y acciones pueden estar expresadas en la preferencia tanto en mayuscula como en minuscula
	 *	Importante: Cualquier modificación sobre la configuración de auditoria requerirá reiniciar la aplicación
	 * */
	protected static HashMap<String, String> auditTables = null;
	
	/**
	 * Determina si la actividad de persistencia debe ser auditada 
	 * @param tableName nombre de la tabla sobre la cual se esta efectuando la actividad de persistencia
	 * @param opType operacion: (I)nsertion, (M)odification, (D)eletion
	 * @return true si debe auditarse la actividad, o false en caso contrario
	 */
	public boolean shouldAudit(String tableName, String opType) {
		try {
			// Recuperar la - eventual - configuracion para una la tabla sobre la que se esta operando
			if (auditTables==null) {
				auditTables = new HashMap<String, String>();
				
				// Recuperar y parsear la preferencia, la cual tiene el siguiente formato: TABLA=ACCIONES:TABLA=ACCIONES
				// Ejemplo: C_OrderLine = IMD : C_Order = DM
				String auditConfig = MPreference.GetCustomPreferenceValue(AUDIT_EVENTS_CONFIGURATION_PREFERENCE, getAD_Client_ID());
				if (Util.isEmpty(auditConfig))
					return false;
				auditConfig = auditConfig.trim().replaceAll(" ", "");
				String[] ruleSet = auditConfig.split(":");

				// Iterar por cada regla y almacenar en la map de auditorias: TableName -> Actions 
				for (String aRule : ruleSet) {
					String[] aRulePart = aRule.split("=");
					String table = aRulePart[0];
					String actions = aRulePart[1];
					auditTables.put(table.toLowerCase(), actions.toLowerCase());
				}
			}

			// Si la tabla sobre la que se esta actuando esta en la map, y la operacion esta incluida, entonces auditar
			return (auditTables.get(tableName.toLowerCase())!=null && auditTables.get(tableName.toLowerCase()).contains(opType.toLowerCase()));
		} catch (Exception e) {
			// Ante cualquier error por esta logica, se omite la auditoria
			e.printStackTrace();
			return false;
		}
	}
	
} // PO
