/*
 * @(#)MQuery.java   12.oct 2007  Versión 2.2
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

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

import org.openXpertya.util.*;

/**
 *  Query Descriptor.
 *  Maintains restrictions (WHERE clause)
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *    Jorg Janke
 *  @version    $Id: MQuery.java,v 1.17 2005/03/11 20:28:32 jjanke Exp $
 */
public class MQuery implements Serializable
{
    /**
     *  Get Query from Parameter
     *  @param ctx context (to determine language)
     *  @param AD_PInstance_ID instance
     *  @param TableName table name
     *  @return where clause
     */
    static public MQuery get (Properties ctx, int AD_PInstance_ID, String TableName)
    {
        s_log.info("AD_PInstance_ID=" + AD_PInstance_ID + ", TableName=" + TableName);
        MQuery query = new MQuery(TableName);
        //  Temporary Tables - add qualifier (not displayed)
        if (TableName.startsWith("T_"))
            query.addRestriction(TableName + ".AD_PInstance_ID=" + AD_PInstance_ID);

        //  How many rows do we have?
        int rows = 0;
        String SQL = "SELECT COUNT(*) FROM AD_PInstance_Para WHERE AD_PInstance_ID=?";
        try
        {
            PreparedStatement pstmt = DB.prepareStatement(SQL);
            pstmt.setInt(1, AD_PInstance_ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                rows = rs.getInt(1);
            rs.close();
            pstmt.close();
        }
        catch (SQLException e1)
        {
            s_log.log(Level.SEVERE, SQL, e1);
        }

        if (rows < 1)
            return query;

        //  Msg.getMsg(Env.getCtx(), "Parameter")
        boolean trl = !Env.isBaseLanguage(ctx, "AD_Process_Para");
        if (!trl)
            SQL = "SELECT ip.ParameterName,ip.P_String,ip.P_String_To,"         //  1..3
                + "ip.P_Number,ip.P_Number_To,"                                 //  4..5
                + "ip.P_Date,ip.P_Date_To, ip.Info,ip.Info_To, "                //  6..9
                + "pp.Name, pp.IsRange "                                        //  10..11
                + "FROM AD_PInstance_Para ip, AD_PInstance i, AD_Process_Para pp "
                + "WHERE i.AD_PInstance_ID=ip.AD_PInstance_ID"
                + " AND pp.AD_Process_ID=i.AD_Process_ID"
                + " AND pp.ColumnName=ip.ParameterName"
                + " AND ip.AD_PInstance_ID=?";
        else
            SQL = "SELECT ip.ParameterName,ip.P_String,ip.P_String_To, ip.P_Number,ip.P_Number_To,"
                + "ip.P_Date,ip.P_Date_To, ip.Info,ip.Info_To, "
                + "ppt.Name, pp.IsRange "
                + "FROM AD_PInstance_Para ip, AD_PInstance i, AD_Process_Para pp, AD_Process_Para_Trl ppt "
                + "WHERE i.AD_PInstance_ID=ip.AD_PInstance_ID"
                + " AND pp.AD_Process_ID=i.AD_Process_ID"
                + " AND pp.ColumnName=ip.ParameterName"
                + " AND pp.AD_Process_Para_ID=ppt.AD_Process_Para_ID"
                + " AND ip.AD_PInstance_ID=?"
                + " AND ppt.AD_Language=?";
        try
        {
            PreparedStatement pstmt = DB.prepareStatement(SQL);
            pstmt.setInt(1, AD_PInstance_ID);
            if (trl)
                pstmt.setString(2, Env.getAD_Language(ctx));
            ResultSet rs = pstmt.executeQuery();
            //  all records
            for (int row = 0; rs.next(); row++)
            {
                if (row == rows)
                {
                    s_log.log(Level.SEVERE, "(Parameter) - more rows than expected");
                    break;
                }
                String ParameterName = rs.getString(1);
                String P_String = rs.getString(2);
                String P_String_To = rs.getString(3);
                //
                Double P_Number = null;
                double d = rs.getDouble(4);
                if (!rs.wasNull())
                    P_Number = new Double(d);
                Double P_Number_To = null;
                d = rs.getDouble(5);
                if (!rs.wasNull())
                    P_Number_To = new Double(d);
                //
                Timestamp P_Date = rs.getTimestamp(6);
                Timestamp P_Date_To = rs.getTimestamp(7);
                //
                String Info = rs.getString(8);
                String Info_To = rs.getString(9);
                //
                String Name = rs.getString(10);
                boolean isRange = "Y".equals(rs.getString(11));
                //
                s_log.fine(ParameterName + " S=" + P_String + "-" + P_String_To
                    + ", N=" + P_Number + "-" + P_Number_To + ", D=" + P_Date + "-" + P_Date_To
                    + "; Name=" + Name + ", Info=" + Info + "-" + Info_To + ", Range=" + isRange);

                //-------------------------------------------------------------
                if (P_String != null)
                {
                    if (P_String_To == null)
                    {
                        if (P_String.indexOf("%") == -1)
                            query.addRestriction(ParameterName, MQuery.EQUAL, 
                                P_String, Name, Info);
                        else
                            query.addRestriction(ParameterName, MQuery.LIKE, 
                                P_String, Name, Info);
                    }
                    else
                        query.addRangeRestriction(ParameterName, 
                            P_String, P_String_To, Name, Info, Info_To);
                }
                //  Number
                else if (P_Number != null || P_Number_To != null)
                {
                    if (P_Number_To == null)
                    {
                        if (isRange)
                            query.addRestriction(ParameterName, MQuery.GREATER_EQUAL, 
                                P_Number, Name, Info);
                        else
                            query.addRestriction(ParameterName, MQuery.EQUAL, 
                                P_Number, Name, Info);
                    }
                    else    //  P_Number_To != null
                    {
                        if (P_Number == null)
                            query.addRestriction("TRUNC("+ParameterName+")", MQuery.LESS_EQUAL, 
                                P_Number_To, Name, Info_To);
                        else
                            query.addRangeRestriction(ParameterName, 
                                P_Number, P_Number_To, Name, Info, Info_To);
                    }
                }
                //  Date
                else if (P_Date != null || P_Date_To != null)
                {
                    if (P_Date_To == null)
                    {
                        if (isRange)
                            query.addRestriction("TRUNC("+ParameterName+")", MQuery.GREATER_EQUAL, 
                                P_Date, Name, Info);
                        else
                            query.addRestriction("TRUNC("+ParameterName+")", MQuery.EQUAL, 
                                P_Date, Name, Info);
                    }
                    else    //  P_Date_To != null
                    {
                        if (P_Date == null)
                            query.addRestriction("TRUNC("+ParameterName+")", MQuery.LESS_EQUAL, 
                                P_Date_To, Name, Info_To);
                        else
                            query.addRangeRestriction("TRUNC("+ParameterName+")", 
                                P_Date, P_Date_To, Name, Info, Info_To);
                    }
                }
            }
            rs.close();
            pstmt.close();
        }
        catch (SQLException e2)
        {
            s_log.log(Level.SEVERE, SQL, e2);
        }
        s_log.info(query.toString());
        return query;
    }   //  get
    
    
    /**
     *  Get Zoom Column Name.
     *  Converts Synonyms like SalesRep_ID to AD_User_ID
     *  @param columnName column name
     *  @return column name
     */
    public static String getZoomColumnName (String columnName)
    {
        if (columnName == null)
            return null;
        if (columnName.equals("SalesRep_ID"))
            return "AD_User_ID";
        if (columnName.equals("C_DocTypeTarget_ID"))
            return "C_DocType_ID";
        if (columnName.equals("Bill_BPartner_ID"))
            return "C_BPartner_ID";
        if (columnName.equals("Bill_Location_ID"))
            return "C_BPartner_Location_ID";
        if (columnName.equals("Account_ID"))
            return "C_ElementValue_ID"; 
        //  See also MTab.validateQuery
        //
        return columnName;
    }   //  getZoomColumnName
    
    /**
     *  Derive Zoom Table Name from column name.
     *  (e.g. drop _ID)
     *  @param columnName  column name
     *  @return table name
     */
    public static String getZoomTableName (String columnName)
    {
        String tableName = getZoomColumnName(columnName);
        int index = tableName.lastIndexOf("_ID");
        if (index != -1)
            return tableName.substring(0, index);
        return tableName;
    }   //  getZoomTableName

    
    /*************************************************************************
     *  Create simple Equal Query.
     *  Creates columnName=value or columnName='value'
     *  @param columnName columnName
     *  @param value value
     *  @return quary
     */
    public static MQuery getEqualQuery (String columnName, Object value)
    {
        MQuery query = new MQuery();
        query.addRestriction(columnName, EQUAL, value);
        return query;
    }   //  getEqualQuery

    /**
     *  Create simple Equal Query.
     *  Creates columnName=value
     *  @param columnName columnName
     *  @param value value
     *  @return quary
     */
    public static MQuery getEqualQuery (String columnName, int value)
    {
        MQuery query = new MQuery();
        if (columnName.endsWith("_ID"))
            query.setTableName(columnName.substring(0, columnName.length()-3));
        query.addRestriction(columnName, EQUAL, new Integer(value));
        return query;
    }   //  getEqualQuery

    /** Static Logger   */
    private static CLogger  s_log   = CLogger.getCLogger (MQuery.class);
    
    
    /**************************************************************************
     *  Constructor w/o table name
     */
    public MQuery ()
    {
    }   //  MQuery

    /**
     *  Constructor
     *  @param TableName Table Name
     */
    public MQuery (String TableName)
    {
        m_TableName = TableName;
    }   //  MQuery

    /**
     *  Constructor get TableNAme from Table
     *  @param AD_Table_ID Table_ID
     */
    public MQuery (int AD_Table_ID)
    {
        String sql = "SELECT TableName FROM AD_Table WHERE AD_Table_ID=?";
        try
        {
            PreparedStatement pstmt = DB.prepareStatement(sql);
            pstmt.setInt (1, AD_Table_ID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                m_TableName = rs.getString(1);
            else
                s_log.log(Level.SEVERE, "Did not find AD_Table_ID=" + AD_Table_ID);
            rs.close();
            pstmt.close();
        }
        catch (SQLException e)
        {
            s_log.log(Level.SEVERE, "MQuery", e);
        }
    }   //  MQuery

    /** Serialization Info  **/
    static final long serialVersionUID = 1511402030597166113L;

    /** Table Name                  */
    private String      m_TableName = "";
    /** List of Restrictions        */
    private ArrayList   m_list = new ArrayList();


    /*************************************************************************/

    public static final String  EQUAL = "=";
    public static final int     EQUAL_INDEX = 0;
    public static final String  NOT_EQUAL = "!=";
    public static final String  LIKE = " LIKE ";
    public static final String  NOT_LIKE = " NOT LIKE ";
    public static final String  ILIKE = " ILIKE ";
    public static final String  NOT_ILIKE = " NOT ILIKE ";
    public static final String  GREATER = ">";
    public static final String  GREATER_EQUAL = ">=";
    public static final String  LESS = "<";
    public static final String  LESS_EQUAL = "<=";
    public static final String  BETWEEN = " BETWEEN ";
    public static final int     BETWEEN_INDEX = 8;
    public static final String  STR_EQUAL = "  ILIKE  ";
    public static final String  STR_NOT_EQUAL = "  NOT ILIKE  ";

    /** Operators for Strings               */
    
    /*
     * Modificación - Disytel - Matías Cap
     * Se crean mensajes para que visualmente al usuario le sea mas legible entender
     * sin utilizar símbolos característicos de lenguajes de programación que el usuario final no tiene idea 
     */
    
    public static final ValueNamePair[] OPERATORS = new ValueNamePair[] {
        new ValueNamePair (EQUAL,           Msg.translate(Env.getCtx(), "Equals")),     //  0
        new ValueNamePair (NOT_EQUAL,       Msg.translate(Env.getCtx(), "OtherThan")),
        // La opcion de sensible a mayus se quitó ya que la búsqueda debe ser
        // flexible, buscando siempre sin case sensitive.
        //new ValueNamePair (LIKE,            Msg.translate(Env.getCtx(), "Like")),
        //new ValueNamePair (NOT_LIKE,        Msg.translate(Env.getCtx(), "NotLike")),
        new ValueNamePair (ILIKE,        	Msg.translate(Env.getCtx(), "Ilike")),
        new ValueNamePair (NOT_ILIKE,       Msg.translate(Env.getCtx(), "NotIlike")),
        new ValueNamePair (GREATER,         Msg.translate(Env.getCtx(), "GreaterThan")),
        new ValueNamePair (GREATER_EQUAL,   Msg.translate(Env.getCtx(), "GreaterEqualTo")),    //  5
        new ValueNamePair (LESS,            Msg.translate(Env.getCtx(), "LessThan")),
        new ValueNamePair (LESS_EQUAL,      Msg.translate(Env.getCtx(), "LessEqualTo")),
        new ValueNamePair (BETWEEN,         Msg.translate(Env.getCtx(), "Between"))    //  8
    };
    /** Operators for IDs                   */
    public static final ValueNamePair[] OPERATORS_ID = new ValueNamePair[] {
        new ValueNamePair (EQUAL,           Msg.translate(Env.getCtx(), "Equals")),     //  0
        new ValueNamePair (NOT_EQUAL,       Msg.translate(Env.getCtx(), "OtherThan"))
    };
    /** Operators for Boolean                   */
    public static final ValueNamePair[] OPERATORS_YN = new ValueNamePair[] {
        new ValueNamePair (EQUAL,           Msg.translate(Env.getCtx(), "Equals"))
    };

    public static final ValueNamePair[] OPERATORS_NUM = new ValueNamePair[] {
        new ValueNamePair (EQUAL,           Msg.translate(Env.getCtx(), "Equals")),     //  0
        new ValueNamePair (NOT_EQUAL,       Msg.translate(Env.getCtx(), "OtherThan")),
        new ValueNamePair (GREATER,         Msg.translate(Env.getCtx(), "GreaterThan")),
        new ValueNamePair (GREATER_EQUAL,   Msg.translate(Env.getCtx(), "GreaterEqualTo")),    //  5
        new ValueNamePair (LESS,            Msg.translate(Env.getCtx(), "LessThan")),
        new ValueNamePair (LESS_EQUAL,      Msg.translate(Env.getCtx(), "LessEqualTo")),
        new ValueNamePair (BETWEEN,         Msg.translate(Env.getCtx(), "Between"))    //  8
    };

    public static final ValueNamePair[] OPERATORS_STR = new ValueNamePair[] {
        new ValueNamePair (STR_EQUAL,           Msg.translate(Env.getCtx(), "Equals")),     //  0
        new ValueNamePair (STR_NOT_EQUAL,       Msg.translate(Env.getCtx(), "OtherThan")),
        new ValueNamePair (ILIKE,            Msg.translate(Env.getCtx(), "Like")),
        new ValueNamePair (NOT_ILIKE,        Msg.translate(Env.getCtx(), "NotLike")),
        new ValueNamePair (GREATER,         Msg.translate(Env.getCtx(), "GreaterThan")),
        new ValueNamePair (GREATER_EQUAL,   Msg.translate(Env.getCtx(), "GreaterEqualTo")),    //  5
        new ValueNamePair (LESS,            Msg.translate(Env.getCtx(), "LessThan")),
        new ValueNamePair (LESS_EQUAL,      Msg.translate(Env.getCtx(), "LessEqualTo")),
        new ValueNamePair (BETWEEN,         Msg.translate(Env.getCtx(), "Between"))    //  8
    };

    
    /*************************************************************************
     *  Add Restriction
     *  @param ColumnName ColumnName
     *  @param Operator Operator, e.g. = != ..
     *  @param Code Code, e.g 0, All%
     *  @param InfoName Display Name
     *  @param InfoDisplay Display of Code (Lookup)
     */
    public void addRestriction (String ColumnName, String Operator,
        Object Code, String InfoName, String InfoDisplay)
    {
        Restriction r = new Restriction (ColumnName, Operator,
            Code, InfoName, InfoDisplay);
        m_list.add(r);
    }   //  addRestriction

    /**
     *  Add Restriction
     *  @param ColumnName ColumnName
     *  @param Operator Operator, e.g. = != ..
     *  @param Code Code, e.g 0, All%
     */
    public void addRestriction (String ColumnName, String Operator,
        Object Code)
    {
        Restriction r = new Restriction (ColumnName, Operator,
            Code, null, null);
        m_list.add(r);
    }   //  addRestriction

    /**
     *  Add Restriction
     *  @param ColumnName ColumnName
     *  @param Operator Operator, e.g. = != ..
     *  @param Code Code, e.g 0
     */
    public void addRestriction (String ColumnName, String Operator,
        int Code)
    {
        Restriction r = new Restriction (ColumnName, Operator,
            new Integer(Code), null, null);
        m_list.add(r);
    }   //  addRestriction

    /**
     *  Add Range Restriction (BETWEEN)
     *  @param ColumnName ColumnName
     *  @param Code Code, e.g 0, All%
     *  @param Code_to Code, e.g 0, All%
     *  @param InfoName Display Name
     *  @param InfoDisplay Display of Code (Lookup)
     *  @param InfoDisplay_to Display of Code (Lookup)
     */
    public void addRangeRestriction (String ColumnName,
        Object Code, Object Code_to,
        String InfoName, String InfoDisplay, String InfoDisplay_to)
    {
        Restriction r = new Restriction (ColumnName, Code, Code_to,
            InfoName, InfoDisplay, InfoDisplay_to);
        m_list.add(r);
    }   //  addRestriction

    /**
     *  Add Range Restriction (BETWEEN)
     *  @param ColumnName ColumnName
     *  @param Code Code, e.g 0, All%
     *  @param Code_to Code, e.g 0, All%
     */
    public void addRangeRestriction (String ColumnName,
        Object Code, Object Code_to)
    {
        Restriction r = new Restriction (ColumnName, Code, Code_to,
            null, null, null);
        m_list.add(r);
    }   //  addRestriction

    /**
     *  Add Restriction
     *  @param r Restriction
     */
    protected void addRestriction (Restriction r)
    {
        m_list.add(r);
    }   //  addRestriction

    /**
     *  Add Restriction
     *  @param whereClause SQL WHERE clause
     */
    public void addRestriction (String whereClause)
    {
        if (whereClause == null || whereClause.trim().length() == 0)
            return;
        Restriction r = new Restriction (whereClause);
        m_list.add(r);
    }   //  addRestriction

    
    /*************************************************************************
     *  Create the resulting Query WHERE Clause
     *  @return Where Clause
     */
    public String getWhereClause ()
    {
        return getWhereClause(false);
    }   //  getWhereClause

    /**
     *  Create the resulting Query WHERE Clause
     *  @param fullyQualified fully qualified Table.ColumnName
     *  @return Where Clause
     */
    public String getWhereClause (boolean fullyQualified)
    {
        boolean qualified = fullyQualified;
        if (qualified && (m_TableName == null || m_TableName.length() == 0))
            qualified = false;
        //
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < m_list.size(); i++)
        {
            Restriction r = (Restriction)m_list.get(i);
            if (i != 0)
                sb.append(r.andCondition ? " AND " : " OR ");
            if (qualified)
                sb.append(r.getSQL(m_TableName));
            else
                sb.append(r.getSQL(null));
        }
        return sb.toString();
    }   //  getWhereClause

    /**
     *  Get printable Query Info
     *  @return info
     */
    public String getInfo ()
    {
        StringBuffer sb = new StringBuffer();
        if (m_TableName != null)
            sb.append(m_TableName).append(": ");
        //
        for (int i = 0; i < m_list.size(); i++)
        {
            Restriction r = (Restriction)m_list.get(i);
            if (i != 0)
                sb.append(r.andCondition ? " AND " : " OR ");
            //
            sb.append(r.getInfoName())
                .append(r.getInfoOperator())
                .append(r.getInfoDisplayAll());
        }
        return sb.toString();
    }   //  getInfo

    
    /**
     *  Create Query WHERE Clause.
     *  Not fully qualified
     *  @param index restriction index
     *  @return Where Clause or "" if not valid
     */
    public String getWhereClause (int index)
    {
        StringBuffer sb = new StringBuffer();
        if (index >= 0 && index < m_list.size())
        {
            Restriction r = (Restriction)m_list.get(index);
            sb.append(r.getSQL(null));
        }
        return sb.toString();
    }   //  getWhereClause

    /**
     *  Get Restriction Count
     *  @return number of restricctions
     */
    public int getRestrictionCount()
    {
        return m_list.size();
    }   //  getRestrictionCount

    /**
     *  Is Query Active
     *  @return true if number of restricctions > 0
     */
    public boolean isActive()
    {
        return m_list.size() != 0;
    }   //  isActive

    /**
     *  Get Table Name
     *  @return Table Name
     */
    public String getTableName ()
    {
        return m_TableName;
    }   //  getTableName

    /**
     *  Set Table Name
     *  @param TableName Table Name
     */
    public void setTableName (String TableName)
    {
        m_TableName = TableName;
    }   //  setTableName

    
    /*************************************************************************
     *  Get ColumnName of index
     *  @param index index
     *  @return ColumnName
     */
    public String getColumnName (int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.ColumnName;
    }   //  getColumnName

    /**
     *  Set ColumnName of index
     *  @param index index
     *  @param ColumnName new column name
     */
    protected void setColumnName (int index, String ColumnName)
    {
        if (index < 0 || index >= m_list.size())
            return;
        Restriction r = (Restriction)m_list.get(index);
        r.ColumnName = ColumnName;
    }   //  setColumnName

    /**
     *  Get Operator of index
     *  @param index index
     *  @return Operator
     */
    public String getOperator (int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.Operator;
    }   //  getOperator

    /**
     *  Get Operator of index
     *  @param index index
     *  @return Operator
     */
    public Object getCode (int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.Code;
    }   //  getCode

    /**
     *  Get Restriction Display of index
     *  @param index index
     *  @return Restriction Display
     */
    public String getInfoDisplay (int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.InfoDisplay;
    }   //  getOperator

    /**
     *  Get TO Restriction Display of index
     *  @param index index
     *  @return Restriction Display
     */
    public String getInfoDisplay_to (int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.InfoDisplay_to;
    }   //  getOperator

    /**
     *  Get Info Name
     *  @param index index
     *  @return Info Name
     */
    public String getInfoName(int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.InfoName;
    }   //  getInfoName

    /**
     *  Get Info Operator
     *  @param index index
     *  @return info Operator
     */
    public String getInfoOperator(int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.getInfoOperator();
    }   //  getInfoOperator

    /**
     *  Get Display with optional To
     *  @param index index
     *  @return info display
     */
    public String getInfoDisplayAll (int index)
    {
        if (index < 0 || index >= m_list.size())
            return null;
        Restriction r = (Restriction)m_list.get(index);
        return r.getInfoDisplayAll();
    }   //  getInfoDisplay

    /**
     *  String representation
     *  @return info
     */
    public String toString()
    {
        if (isActive())
            return getWhereClause(true);
        return "MQuery[" + m_TableName + ",Restrictions=0]";
    }   //  toString
    
    /**
     *  Get Display Name
     *  @param ctx context
     *  @return display Name
     */
    public String getDisplayName(Properties ctx)
    {
        String keyColumn = null;
        if (m_TableName != null)
            keyColumn = m_TableName + "_ID";
        else
            keyColumn = getColumnName(0);
        String retValue = Msg.translate(ctx, keyColumn);
        if (retValue != null && retValue.length() > 0)
            return retValue;
        return m_TableName;
    }   //  getDisplayName

    /**
     *  Clone Query
     *  @return Query
     */
    public MQuery deepCopy()
    {
        MQuery newQuery = new MQuery(m_TableName);
        for (int i = 0; i < m_list.size(); i++)
            newQuery.addRestriction((Restriction)m_list.get(i));
        return newQuery;
    }   //  clone


	public void setRecordCount(int count) {
		m_recordCount = count;
	}	//	setRecordCount
	/**	Record Count				*/
	private int			m_recordCount = 999999;


	/**
	 * 
	 * @param tableName
	 */
	public void setZoomTableName(String tableName) {
		m_zoomTable = tableName;
	}
	
	public void setZoomColumnName(String column) {
		m_zoomColumn = column;
	}

	
	private String m_zoomTable;
	
	private String m_zoomColumn;

	private Object m_zoomValue;
	
	public boolean isNewRecordQuery()
	{
		return m_newRecord;
	}	//	isNewRecord
	/** New Record Query			*/
	private boolean		m_newRecord = false;

	public int getRecordCount()
	{
		return m_recordCount;
	}	//	getRecordCount
	
	public String getZoomColumnName() {
		return m_zoomColumn;
	}

	public String getZoomTableName() {
		return m_zoomTable;
	}

	public Object getZoomValue() {
		return m_zoomValue;
	}

	/**
	 * 	Create No Record query.
	 * 	@param tableName table name
	 * 	@param newRecord new Record Indicator (2=3) 
	 * 	@return query
	 */
	public static MQuery getNoRecordQuery (String tableName, boolean newRecord)
	{
		MQuery query = new MQuery(tableName);
		if (newRecord)
			query.addRestriction(NEWRECORD);
		else
			query.addRestriction("1=2");
		query.setRecordCount(0);
		return query;
	}	//	getNoRecordQuery
	/** New Record String			*/
	private static final String	NEWRECORD = "2=3";

	public void setZoomValue(Object value) {
		m_zoomValue = value;
	}

	
}   //  MQuery

/*****************************************************************************
 *  Query Restriction
 */
class Restriction  implements Serializable
{
    /**
     *  Restriction
     *  @param ColumnName ColumnName
     *  @param Operator Operator, e.g. = != ..
     *  @param Code Code, e.g 0, All%
     *  @param InfoName Display Name
     *  @param InfoDisplay Display of Code (Lookup)
     */
    Restriction (String ColumnName, String Operator,
        Object Code, String InfoName, String InfoDisplay)
    {
        this.ColumnName = ColumnName.trim();
        if (InfoName != null)
            this.InfoName = InfoName;
        else
            this.InfoName = this.ColumnName;
        //
        this.Operator = Operator;
        //  Boolean
        if (Code instanceof Boolean)
            this.Code = ((Boolean)Code).booleanValue() ? "Y" : "N";
        else if (Code instanceof KeyNamePair)
            this.Code = new Integer(((KeyNamePair)Code).getKey());
        else if (Code instanceof ValueNamePair)
            this.Code = ((ValueNamePair)Code).getValue();
        else
            this.Code = Code;
        //  clean code
        if (this.Code instanceof String)
        {
            if (this.Code.toString().startsWith("'"))
                this.Code = this.Code.toString().substring(1);
            if (this.Code.toString().endsWith("'"))
                this.Code = this.Code.toString().substring(0, this.Code.toString().length()-2);
        }
        if (InfoDisplay != null)
            this.InfoDisplay = InfoDisplay.trim();
        else if (this.Code != null)
            this.InfoDisplay = this.Code.toString();
    }   //  Restriction

    /**
     *  Range Restriction (BETWEEN)
     *  @param ColumnName ColumnName
     *  @param Code Code, e.g 0, All%
     *  @param Code_to Code, e.g 0, All%
     *  @param InfoName Display Name
     *  @param InfoDisplay Display of Code (Lookup)
     *  @param InfoDisplay_to Display of Code (Lookup)
     */
    Restriction (String ColumnName,
        Object Code, Object Code_to,
        String InfoName, String InfoDisplay, String InfoDisplay_to)
    {
        this (ColumnName, MQuery.BETWEEN, Code, InfoName, InfoDisplay);

        //  Code_to
        this.Code_to = Code_to;
        if (this.Code_to instanceof String)
        {
            if (this.Code_to.toString().startsWith("'"))
                this.Code_to = this.Code_to.toString().substring(1);
            if (this.Code_to.toString().endsWith("'"))
                this.Code_to = this.Code_to.toString().substring(0, this.Code_to.toString().length()-2);
        }
        //  InfoDisplay_to
        if (InfoDisplay_to != null)
            this.InfoDisplay_to = InfoDisplay_to.trim();
        else if (this.Code_to != null)
            this.InfoDisplay_to = this.Code_to.toString();
    }   //  Restriction

    /**
     *  Create Restriction with dircet WHERE clause
     *  @param whereClause SQL WHERE Clause
     */
    Restriction (String whereClause)
    {
        DircetWhereClause = whereClause;
    }   //  Restriction

    /** Direct Where Clause */
    protected String    DircetWhereClause = null;
    /** Column Name         */
    protected String    ColumnName;
    /** Name                */
    protected String    InfoName;
    /** Operator            */
    protected String    Operator;
    /** SQL Where Code      */
    protected Object    Code;
    /** Info                */
    protected String    InfoDisplay;
    /** SQL Where Code To   */
    protected Object    Code_to;
    /** Info To             */
    protected String    InfoDisplay_to;
    /** And/Or Condition    */
    protected boolean   andCondition = true;

    /**
     *  Return SQL construct for this restriction
     *  @param tableName optional table name
     *  @return SQL WHERE construct
     */
    public String getSQL (String tableName)
    {
        if (DircetWhereClause != null)
            return DircetWhereClause;
        //
        StringBuffer sb = new StringBuffer();
        if (tableName != null && tableName.length() > 0)
        {
            //  Assumes - REPLACE(INITCAP(variable),'s','X') or UPPER(variable)
            int pos = ColumnName.lastIndexOf('(')+1;    //  including (
            int end = ColumnName.indexOf(')');
            //  We have a Function in the ColumnName
            if (pos != -1 && end != -1)
                sb.append(ColumnName.substring(0, pos))
                    .append(tableName).append(".").append(ColumnName.substring(pos, end))
                    .append(ColumnName.substring(end));
            else
                sb.append(tableName).append(".").append(ColumnName);
        }
        else
            sb.append(ColumnName);
        //
        sb.append(Operator);
        // Líneas comentadas para no poner por default los comodines
        /*
        if(Operator.equals(MQuery.ILIKE) || Operator.equals(MQuery.LIKE) || Operator.equals(MQuery.NOT_ILIKE) || Operator.equals(MQuery.NOT_LIKE)){
        	StringBuffer codePosta = new StringBuffer((String)Code);
        	if(codePosta.charAt(0) != '%'){
        		codePosta.insert(0, '%');
        	}
        	if(codePosta.charAt(codePosta.length() - 1) != '%'){
        		codePosta.insert(codePosta.length(), '%');
        	}
        	Code = codePosta.toString();
        }
        */
        if (Code instanceof String)
            sb.append(DB.TO_STRING(Code.toString()));
        else if (Code instanceof Timestamp)
            sb.append(DB.TO_DATE((Timestamp)Code));
        else
            sb.append(Code);
        //  Between
    //  if (Code_to != null && InfoDisplay_to != null)
        if (MQuery.BETWEEN.equals(Operator))
        {
            sb.append(" AND ");
            if (Code_to instanceof String)
                sb.append(DB.TO_STRING(Code_to.toString()));
            else if (Code_to instanceof Timestamp)
                sb.append(DB.TO_DATE((Timestamp)Code_to));
            else
                sb.append(Code_to);
        }
        return sb.toString();
    }   //  getSQL

    /**
     *  Get String Representation
     *  @return info
     */
    public String toString()
    {
        return getSQL(null);
    }   //  toString

    /**
     *  Get Info Name
     *  @return Info Name
     */
    public String getInfoName()
    {
        return InfoName;
    }   //  getInfoName

    /**
     *  Get Info Operator
     *  @return info Operator
     */
    public String getInfoOperator()
    {
        for (int i = 0; i < MQuery.OPERATORS.length; i++)
        {
            if (MQuery.OPERATORS[i].getValue().equals(Operator)){
            	//Si es alguno de los operadores que se encuentran en el if, retorno el valor en el arreglo 
            	if(MQuery.OPERATORS[i].getValue().equals(MQuery.BETWEEN) 
            			|| MQuery.OPERATORS[i].getValue().equals(MQuery.ILIKE) 
            			|| MQuery.OPERATORS[i].getValue().equals(MQuery.LIKE)
            			|| MQuery.OPERATORS[i].getValue().equals(MQuery.NOT_ILIKE)
            			|| MQuery.OPERATORS[i].getValue().equals(MQuery.NOT_LIKE)){
            		return " "+MQuery.OPERATORS[i].getName()+" ";
            	}
            	//sino retorno el valor de la constante
                return " "+MQuery.OPERATORS[i].getValue()+" ";
            }
        }
        return Operator;
    }   //  getInfoOperator

    /**
     *  Get Display with optional To
     *  @return info display
     */
    public String getInfoDisplayAll()
    {
        if (InfoDisplay_to == null)
            return InfoDisplay;
        StringBuffer sb = new StringBuffer(InfoDisplay);
        sb.append(" - ").append(InfoDisplay_to);
        return sb.toString();
    }   //  getInfoDisplay

    
}   //  Restriction


/*
 * @(#)MQuery.java   02.jul 2007
 * 
 *  Fin del fichero MQuery.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
