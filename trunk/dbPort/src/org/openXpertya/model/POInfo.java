/*
 * @(#)POInfo.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Persistet Object Info.
 *  Provides structural information
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: POInfo.java,v 1.29 2005/05/14 05:32:16 jjanke Exp $
 */
public class POInfo implements Serializable {

    /** Used by Remote FinReport */
    static final long	serialVersionUID	= -5976719579744948419L;

    /** Cache of POInfo */
    private static CCache	s_cache	= new CCache("POInfo", 200);

    /** Context */
    private Properties	m_ctx	= null;

    /** Columns */
    private POInfoColumn[]	m_columns	= null;

    /** Table Name */
    private String	m_TableName	= null;

    /** Access Level */
    private String	m_AccessLevel	= M_Table.ACCESSLEVEL_Organization;

    /** Table_ID */
    private int	m_AD_Table_ID	= 0;

    /** Table has Key Column */
    private boolean	m_hasKeyColumn	= false;

    private static CLogger log= CLogger.getCLogger( POInfo.class );
    
	/**************************************************************************
	 *  Create Persistent Info
	 *  @param ctx context
	 *  @param AD_Table_ID AD_ Table_ID
	 * 	@param baseLanguageOnly get in base language
	 *  @param trxName transaction name
	 */
	private POInfo (Properties ctx, int AD_Table_ID, boolean baseLanguageOnly, String trxName)
	{
		m_ctx = ctx;
		m_AD_Table_ID = AD_Table_ID;
		boolean baseLanguage = baseLanguageOnly ? true : Env.isBaseLanguage(m_ctx, "AD_Table");
		loadInfo (baseLanguage, trxName);
	}   //  PInfo

    /**
     *  Create Persistent Info
     *  @param ctx context
     *  @param AD_Table_ID AD_ Table_ID
     *      @param baseLanguageOnly get in base language
     */
    private POInfo(Properties ctx, int AD_Table_ID, boolean baseLanguageOnly) {

        m_ctx		= ctx;
        m_AD_Table_ID	= AD_Table_ID;

        boolean	baseLanguage	= baseLanguageOnly
                                  ? true
                                  : Env.isBaseLanguage(m_ctx, "AD_Table");

        loadInfo(baseLanguage);

    }		// PInfo

    
	/**
	 *  Load Table/Column Info
	 * 	@param baseLanguage in English
	 *  @param trxName
	 */
	/**	Table needs keep log*/
	private boolean 	m_IsChangeLog = false;
	private void loadInfo (boolean baseLanguage, String trxName)
	{
		ArrayList<POInfoColumn> list = new ArrayList<POInfoColumn>(15);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.TableName, c.ColumnName,c.AD_Reference_ID,"    //  1..3
			+ "c.IsMandatory,c.IsUpdateable,c.DefaultValue,"                //  4..6
			+ "e.Name,e.Description, c.AD_Column_ID, "						//  7..9
			+ "c.IsKey,c.IsParent, "										//	10..11
			+ "c.AD_Reference_Value_ID, vr.Code, "							//	12..13
			+ "c.FieldLength, c.ValueMin, c.ValueMax, c.IsTranslated, "		//	14..17
			+ "t.AccessLevel, c.ColumnSQL, c.IsEncrypted, "					// 18..20
			+ "'Y' as IsAllowLogging,t.IsChangeLog ");											// 21
		sql.append("FROM AD_Table t"
			+ " INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID)"
			+ " LEFT OUTER JOIN AD_Val_Rule vr ON (c.AD_Val_Rule_ID=vr.AD_Val_Rule_ID)"
			+ " INNER JOIN AD_Element");
		if (!baseLanguage)
			sql.append("_Trl");
		sql.append(" e "
			+ " ON (c.AD_Element_ID=e.AD_Element_ID) "
			+ "WHERE t.AD_Table_ID=?"
			+ " AND c.IsActive='Y'");
		if (!baseLanguage)
			sql.append(" AND e.AD_Language='").append(Env.getAD_Language(m_ctx)).append("'");
		//
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), trxName);
			pstmt.setInt(1, m_AD_Table_ID);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				if (m_TableName == null)
					m_TableName = rs.getString(1);
				String ColumnName = rs.getString(2);
				int AD_Reference_ID = rs.getInt(3);
				boolean IsMandatory = "Y".equals(rs.getString(4));
				boolean IsUpdateable = "Y".equals(rs.getString(5));
				String DefaultLogic = rs.getString(6);
				String Name = rs.getString(7);
				String Description = rs.getString(8);
				int AD_Column_ID = rs.getInt(9);
				boolean IsKey = "Y".equals(rs.getString(10));
				if (IsKey)
					m_hasKeyColumn = true;
				boolean IsParent = "Y".equals(rs.getString(11));
				int AD_Reference_Value_ID = rs.getInt(12);
				String ValidationCode = rs.getString(13);
				int FieldLength = rs.getInt(14);
				String ValueMin = rs.getString(15);
				String ValueMax = rs.getString(16);
				boolean IsTranslated = "Y".equals(rs.getString(17));
				//
				m_AccessLevel = rs.getString(18);
				String ColumnSQL = rs.getString(19);
				boolean IsEncrypted = "Y".equals(rs.getString(20));
				boolean IsAllowLogging = "Y".equals(rs.getString(21));
				m_IsChangeLog="Y".equals(rs.getString(22));

				POInfoColumn col = new POInfoColumn (
					AD_Column_ID, ColumnName, ColumnSQL, AD_Reference_ID,
					IsMandatory, IsUpdateable,
					DefaultLogic, Name, Description,
					IsKey, IsParent,
					AD_Reference_Value_ID, ValidationCode,
					FieldLength, ValueMin, ValueMax,
					IsTranslated, IsEncrypted,
					IsAllowLogging);
				list.add(col);
			}
		}
		catch (SQLException e)
		{
			CLogger.get().log(Level.SEVERE, sql.toString(), e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		//  convert to array
		m_columns = new POInfoColumn[list.size()];
		list.toArray(m_columns);
	}   //  loadInfo

    
    /**
     *  Load Table/Column Info
     *      @param baseLanguage in English
     */
    private void loadInfo(boolean baseLanguage) {

        ArrayList	list	= new ArrayList(15);
        StringBuffer	sql	= new StringBuffer();

        sql.append("SELECT t.TableName, c.ColumnName,c.AD_Reference_ID,"		// 1..3
                   + "c.IsMandatory,c.IsUpdateable,c.DefaultValue,"			// 4..6
                   + "e.Name,e.Description, c.AD_Column_ID, "				// 7..9
                   + "c.IsKey,c.IsParent, "						// 10..11
                   + "c.AD_Reference_Value_ID, vr.Code, "				// 12..13
                   + "c.FieldLength, c.ValueMin, c.ValueMax, c.IsTranslated, "		// 14..17
                   + "t.AccessLevel, c.ColumnSQL ");					// 18..19
        sql.append("FROM AD_Table t" + " INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID)" + " LEFT OUTER JOIN AD_Val_Rule vr ON (c.AD_Val_Rule_ID=vr.AD_Val_Rule_ID)" + " INNER JOIN AD_Element");

        if (!baseLanguage) {
            sql.append("_Trl");
        }

        sql.append(" e " + " ON (c.AD_Element_ID=e.AD_Element_ID) " + "WHERE t.AD_Table_ID=?" + " AND c.IsActive='Y'");

        if (!baseLanguage) {
            sql.append(" AND e.AD_Language='").append(Env.getAD_Language(m_ctx)).append("'");
        }

        sql.append(" ORDER BY c.ColumnName ");
        //
        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString(), PluginUtils.getPluginInstallerTrxName());

            pstmt.setInt(1, m_AD_Table_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                if (m_TableName == null) {
                    m_TableName	= rs.getString(1);
                }

                String	ColumnName	= rs.getString(2);
                int	AD_Reference_ID	= rs.getInt(3);
                boolean	IsMandatory	= "Y".equals(rs.getString(4));
                boolean	IsUpdateable	= "Y".equals(rs.getString(5));
                String	DefaultLogic	= rs.getString(6);
                String	Name		= rs.getString(7);
                String	Description	= rs.getString(8);
                int	AD_Column_ID	= rs.getInt(9);
                boolean	IsKey		= "Y".equals(rs.getString(10));

                if (IsKey) {
                    m_hasKeyColumn	= true;
                }

                boolean	IsParent		= "Y".equals(rs.getString(11));
                int	AD_Reference_Value_ID	= rs.getInt(12);
                String	ValidationCode		= rs.getString(13);
                int	FieldLength		= rs.getInt(14);
                String	ValueMin		= rs.getString(15);
                String	ValueMax		= rs.getString(16);
                boolean	IsTranslated		= "Y".equals(rs.getString(17));

                //
                m_AccessLevel	= rs.getString(18);

                String		ColumnSQL	= rs.getString(19);
                POInfoColumn	col		= new POInfoColumn(AD_Column_ID, ColumnName, ColumnSQL, AD_Reference_ID, IsMandatory, IsUpdateable, DefaultLogic, Name, Description, IsKey, IsParent, AD_Reference_Value_ID, ValidationCode, FieldLength, ValueMin, ValueMax, IsTranslated);

                list.add(col);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            CLogger.get().log(Level.SEVERE, sql.toString(), e);
        }

        // convert to array
        m_columns	= new POInfoColumn[list.size()];
        list.toArray(m_columns);

    }		// loadInfo

    /**
     *  String representation
     *  @return String Representation
     */
    public String toString() {
        return "POInfo[" + getTableName() + ",AD_Table_ID=" + getAD_Table_ID() + "]";
    }		// toString

    /**
     *  String representation for index
     *      @param index column index
     *  @return String Representation
     */
    public String toString(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return "POInfo[" + getTableName() + "-(InvalidColumnIndex=" + index + ")]";
        }

        return "POInfo[" + getTableName() + "-" + m_columns[index].toString() + "]";

    }		// toString

    /**
     *  Validate Content
     *  @param index index
     *      @param value new Value
     *  @return null if all valid otherwise error message
     */
    public String validate(int index, Object value) {

        if ((index < 0) || (index >= m_columns.length)) {
            return "RangeError";
        }

        // Mandatory (i.e. not null
        if (m_columns[index].IsMandatory && (value == null)) {
            return "IsMandatory";
        }

        if (value == null) {
            return null;
        }

        // Length ignored
        //
        if (m_columns[index].ValueMin != null) {

            BigDecimal	value_BD	= null;

            try {

                if (m_columns[index].ValueMin_BD != null) {
                    value_BD	= new BigDecimal(value.toString());
                }

            } catch (Exception ex) {}

            // Both are Numeric
            if ((m_columns[index].ValueMin_BD != null) && (value_BD != null)) {		// error: 1 - 0 => 1  -  OK: 1 - 1 => 0 & 1 - 10 => -1

                int	comp	= m_columns[index].ValueMin_BD.compareTo(value_BD);

                if (comp > 0) {
                    return "MinValue=" + m_columns[index].ValueMin_BD + "(" + m_columns[index].ValueMin + ")" + " - compared with Numeric Value=" + value_BD + "(" + value + ")" + " - results in " + comp;
                }

            } else	// String
            {

                int	comp	= m_columns[index].ValueMin.compareTo(value.toString());

                if (comp > 0) {
                    return "MinValue=" + m_columns[index].ValueMin + " - compared with String Value=" + value + " - results in " + comp;
                }
            }
        }

        if (m_columns[index].ValueMax != null) {

            BigDecimal	value_BD	= null;

            try {

                if (m_columns[index].ValueMax_BD != null) {
                    value_BD	= new BigDecimal(value.toString());
                }

            } catch (Exception ex) {}

            // Both are Numeric
            if ((m_columns[index].ValueMax_BD != null) && (value_BD != null)) {		// error 12 - 20 => -1  -  OK: 12 - 12 => 0 & 12 - 10 => 1

                int	comp	= m_columns[index].ValueMax_BD.compareTo(value_BD);

                if (comp < 0) {
                    return "MaxValue=" + m_columns[index].ValueMax_BD + "(" + m_columns[index].ValueMax + ")" + " - compared with Numeric Value=" + value_BD + "(" + value + ")" + " - results in " + comp;
                }

            } else	// String
            {

                int	comp	= m_columns[index].ValueMax.compareTo(value.toString());

                if (comp < 0) {
                    return "MaxValue=" + m_columns[index].ValueMax + " - compared with String Value=" + value + " - results in " + comp;
                }
            }
        }

        return null;

    }		// validate

    //~--- get methods --------------------------------------------------------
   
    /**
     *  Get AD_Table_ID
     *  @return AD_Table_ID
     */
    public int getAD_Table_ID() {
        return m_AD_Table_ID;
    }		// getAD_Table_ID

    /**
     *      Get Table Access Level
     *      @return M_Table.ACCESS..
     */
    public String getAccessLevel() {
        return m_AccessLevel;
    }		// getAccessLevel

    /**
     *  Get Column
     *  @param index index
     *  @return column
     */
    protected POInfoColumn getColumn(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        return m_columns[index];

    }		// getColumn

    /**
     *  Get Column Class
     *  @param index index
     *  @return Class
     */
    public Class getColumnClass(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        return m_columns[index].ColumnClass;

    }		// getColumnClass

    /**
     *  Get ColumnCount
     *  @return column count
     */
    public int getColumnCount() {
        return m_columns.length;
    }		// getColumnCount

    /**
     *  Get Column Description
     *  @param index index
     *  @returncolumn description
     *
     * @return
     */
    public String getColumnDescription(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        return m_columns[index].ColumnDescription;

    }		// getColumnDescription

    /**
     *  Get Column Display Type
     *  @param index index
     *  @return DisplayType
     */
    public int getColumnDisplayType(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return DisplayType.String;
        }

        return m_columns[index].DisplayType;

    }		// getColumnDisplayType

    /**
     *  Get Column Index
     *  @param AD_Column_ID column
     *  @return index of column with ColumnName or -1 if not found
     */
    public int getColumnIndex(int AD_Column_ID) {

        for (int i = 0; i < m_columns.length; i++) {

            if (AD_Column_ID == m_columns[i].AD_Column_ID) {
                return i;
            }
        }

        return -1;

    }		// getColumnIndex

    /**
     *  Get Column Index
     *  @param ColumnName column name
     *  @return index of column with ColumnName or -1 if not found
     */
    public int getColumnIndex(String ColumnName) {
        for (int i = 0; i < m_columns.length; i++) {
            if (ColumnName.equals(m_columns[i].ColumnName)) {
                return i;
            }
        }

        return -1;

    }		// getColumnIndex

    
    public int getColumnIndexIgnoreCase(String ColumnName) {
        for (int i = 0; i < m_columns.length; i++) {
            if (ColumnName.equalsIgnoreCase(m_columns[i].ColumnName)) {
                return i;
            }
        }
        return -1;
    }		// getColumnIndexIgnoreCase
 
    
    public int getAD_Column_ID(String ColumnName){
    	for (int i = 0; i < m_columns.length; i++) {
            if (ColumnName.equalsIgnoreCase(m_columns[i].ColumnName)) {
                return m_columns[i].AD_Column_ID;
            }
        }
        return -1;
    }
    
    /**
     *  Get Column Label
     *  @param index index
     *  @return column label
     */
    public String getColumnLabel(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        return m_columns[index].ColumnLabel;

    }		// getColumnLabel

    /**
     *  Get Lookup
     *  @param index index
     *  @return Lookup
     */
    public Lookup getColumnLookup(int index) {

        if (!isColumnLookup(index)) {
            return null;
        }

        //
        int	WindowNo	= 0;

        // List, Table, TableDir
        Lookup	lookup	= null;

        try {
            lookup	= MLookupFactory.get(m_ctx, WindowNo, m_columns[index].AD_Column_ID, m_columns[index].DisplayType, Env.getLanguage(m_ctx), m_columns[index].ColumnName, m_columns[index].AD_Reference_Value_ID, m_columns[index].IsParent, m_columns[index].ValidationCode);
        } catch (Exception e) {
            lookup	= null;		// cannot create Lookup
        }

        return lookup;

        /** @todo other lookup types */

    }		// getColumnLookup

    /**
     *  Get Column Name
     *  @param index index
     *  @return ColumnName column name
     */
    public String getColumnName(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        return m_columns[index].ColumnName;

    }		// getColumnName

    /**
     *  Get Column SQL or Column Name
     *  @param index index
     *  @return ColumnSQL column sql or name
     */
    public String getColumnSQL(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        if ((m_columns[index].ColumnSQL != null) && (m_columns[index].ColumnSQL.length() > 0)) {
            return m_columns[index].ColumnSQL + " AS " + m_columns[index].ColumnName;
        }

        return m_columns[index].ColumnName;

    }		// getColumnSQL

    /**
     *  Get Column Default Logic
     *  @param index index
     *  @return Default Logic
     */
    public String getDefaultLogic(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return null;
        }

        return m_columns[index].DefaultLogic;

    }		// getDefaultLogic

    /**
     *  Get Column FieldLength
     *  @param index index
     *  @return field length
     */
    public int getFieldLength(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return 0;
        }

        return m_columns[index].FieldLength;

    }		// getFieldLength

    
    /**
     * Limpia una key de la cache
     * @param AD_Table_ID
     */
    public static void clearKey(int AD_Table_ID)
    {
    	s_cache.remove(new Integer(AD_Table_ID));
    }
    
	/**
	 *  POInfo Factory
	 *  @param ctx context
	 *  @param AD_Table_ID AD_Table_ID
	 *  @param trxName Transaction name
	 *  @return POInfo
	 */
	public static POInfo getPOInfo (Properties ctx, int AD_Table_ID, String trxName)
	{
		Integer key = new Integer(AD_Table_ID);
		POInfo retValue = (POInfo)s_cache.get(key);
		if (retValue == null)
		{
			retValue = new POInfo(ctx, AD_Table_ID, false, trxName);
			if (retValue.getColumnCount() == 0)
				//	May be run before Language verification
				retValue = new POInfo(ctx, AD_Table_ID, true, trxName);
			else
				s_cache.put(key, retValue);
		}
		return retValue;
	}   //  getPOInfo
    
    /**
     *  POInfo Factory
     *  @param ctx context
     *  @param AD_Table_ID AD_Table_ID
     *  @return POInfo
     */
    public static POInfo getPOInfo(Properties ctx, int AD_Table_ID) {

        Integer	key		= new Integer(AD_Table_ID);
        POInfo	retValue	= (POInfo) s_cache.get(key);

        if (retValue == null) {

            retValue	= new POInfo(ctx, AD_Table_ID, false);

            if (retValue.getColumnCount() == 0) {

                // May be run before Language verification
                retValue	= new POInfo(ctx, AD_Table_ID, true);
            } else {
                s_cache.put(key, retValue);
            }
        }

        return retValue;

    }		// getPOInfo

    /**
     *  Get Table Name
     *  @return Table Name
     */
    public String getTableName() {
        return m_TableName;
    }		// getTableName

    /**
     *      Table has a Key Column
     *      @return true if has a key column
     */
    public boolean hasKeyColumn() {
        return m_hasKeyColumn;
    }		// hasKeyColumn

    /**
     *  Is Column Key
     *  @param index index
     *  @return true if column is the key
     */
    public boolean isColumnKey(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return false;
        }

        return m_columns[index].IsKey;

    }		// isColumnKey

    /**
     *  Is Lookup Column
     *  @param index index
     *  @return true if it is a lookup column
     */
    public boolean isColumnLookup(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return false;
        }

        return DisplayType.isLookup(m_columns[index].DisplayType);

    }		// isColumnLookup

    /**
     *  Is Column Mandatory
     *  @param index index
     *  @return true if column mandatory
     */
    public boolean isColumnMandatory(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return false;
        }

        return m_columns[index].IsMandatory;

    }		// isMandatory

    /**
     *  Is Column Parent
     *  @param index index
     *  @return true if column is a Parent
     */
    public boolean isColumnParent(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return false;
        }

        return m_columns[index].IsParent;

    }		// isColumnParent

    /**
     *  Is Column Translated
     *  @param index index
     *  @return true if column is translated
     */
    public boolean isColumnTranslated(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return false;
        }

        return m_columns[index].IsTranslated;

    }		// isColumnTranslated

    /**
     *  Is Column Updateable
     *  @param index index
     *  @return true if column updateable
     */
    public boolean isColumnUpdateable(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return false;
        }
        
        /* Permitir actualizar valores de tabla de traducción: AD_Language_ID y Referencia a la Tabla  */
        if (	m_TableName.toLowerCase().endsWith("_trl")  )
        	return true;
        
        /* Las tablas treenode% deben permitir almacenar */
        if (	m_TableName.toLowerCase().startsWith("ad_treenode"))
        	return true;

        return m_columns[index].IsUpdateable;

    }		// isUpdateable

    /**
     *  Is Table Translated
     *  @return true if table is translated
     */
    public boolean isTranslated() {

        for (int i = 0; i < m_columns.length; i++) {

            if (m_columns[i].IsTranslated) {
                return true;
            }
        }

        return false;

    }		// isTranslated

    /**
     *  Is Column Virtal?
     *  @param index index
     *  @return true if column is virtual
     */
    public boolean isVirtualColumn(int index) {

        if ((index < 0) || (index >= m_columns.length)) {
            return true;
        }

        if ((m_columns[index].ColumnSQL != null) && (m_columns[index].ColumnSQL.length() > 0)) {
            return true;
        }

        return false;

    }		// isVirtualColumn

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Column Updateable
     *  @param index index
     *  @param updateable column updateable
     */
    public void setColumnUpdateable(int index, boolean updateable) {

        if ((index < 0) || (index >= m_columns.length)) {
            return;
        }

        m_columns[index].IsUpdateable	= updateable;

    }		// setColumnUpdateable

    /**
     *      Set all columns updateable
     *      @param updateable updateable
     */
    public void setUpdateable(boolean updateable) {

        for (int i = 0; i < m_columns.length; i++) {
            m_columns[i].IsUpdateable	= updateable;
        }

    }		// setUpdateable
    
	/**
	 * Build select clause
	 * @return stringbuffer
	 */
	public StringBuffer buildSelect()
	{
		StringBuffer sql = new StringBuffer("SELECT ");
		int size = getColumnCount();
		for (int i = 0; i < size; i++)
		{
			if (i != 0)
				sql.append(",");
			sql.append(getColumnSQL(i));	//	Normal and Virtual Column
		}
		sql.append(" FROM ").append(getTableName());
		return sql;
	}

}	// POInfo



/*
 * @(#)POInfo.java   02.jul 2007
 * 
 *  Fin del fichero POInfo.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
