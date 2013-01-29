/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_SearchDefinition
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 18:03:58.696 */
public class X_AD_SearchDefinition extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_SearchDefinition (Properties ctx, int AD_SearchDefinition_ID, String trxName)
{
super (ctx, AD_SearchDefinition_ID, trxName);
/** if (AD_SearchDefinition_ID == 0)
{
setAD_SearchDefinition_ID (0);
setAD_Table_ID (0);
setAD_Window_ID (0);
setDataType (null);
setIsDefault (false);
setSearchType (null);
}
 */
}
/** Load Constructor */
public X_AD_SearchDefinition (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_SearchDefinition");

/** TableName=AD_SearchDefinition */
public static final String Table_Name="AD_SearchDefinition";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_SearchDefinition");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_SearchDefinition[").append(getID()).append("]");
return sb.toString();
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
if (AD_Column_ID <= 0) set_Value ("AD_Column_ID", null);
 else 
set_Value ("AD_Column_ID", new Integer(AD_Column_ID));
}
/** Get Column.
Column in the table */
public int getAD_Column_ID() 
{
Integer ii = (Integer)get_Value("AD_Column_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_SearchDefinition_ID */
public void setAD_SearchDefinition_ID (int AD_SearchDefinition_ID)
{
set_ValueNoCheck ("AD_SearchDefinition_ID", new Integer(AD_SearchDefinition_ID));
}
/** Get AD_SearchDefinition_ID */
public int getAD_SearchDefinition_ID() 
{
Integer ii = (Integer)get_Value("AD_SearchDefinition_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Window.
Data entry or display window */
public void setAD_Window_ID (int AD_Window_ID)
{
set_Value ("AD_Window_ID", new Integer(AD_Window_ID));
}
/** Get Window.
Data entry or display window */
public int getAD_Window_ID() 
{
Integer ii = (Integer)get_Value("AD_Window_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Data Type.
Type of data */
public void setDataType (String DataType)
{
if (DataType == null) throw new IllegalArgumentException ("DataType is mandatory");
if (DataType.length() > 1)
{
log.warning("Length > 1 - truncated");
DataType = DataType.substring(0,1);
}
set_Value ("DataType", DataType);
}
/** Get Data Type.
Type of data */
public String getDataType() 
{
return (String)get_Value("DataType");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
public static final int PO_WINDOW_ID_AD_Reference_ID = MReference.getReferenceID("AD_Window");
/** Set PO Window.
Purchase Order Window */
public void setPO_Window_ID (int PO_Window_ID)
{
if (PO_Window_ID <= 0) set_Value ("PO_Window_ID", null);
 else 
set_Value ("PO_Window_ID", new Integer(PO_Window_ID));
}
/** Get PO Window.
Purchase Order Window */
public int getPO_Window_ID() 
{
Integer ii = (Integer)get_Value("PO_Window_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Query */
public void setQuery (String Query)
{
if (Query != null && Query.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Query = Query.substring(0,2000);
}
set_Value ("Query", Query);
}
/** Get Query */
public String getQuery() 
{
return (String)get_Value("Query");
}
/** Set SearchType */
public void setSearchType (String SearchType)
{
if (SearchType == null) throw new IllegalArgumentException ("SearchType is mandatory");
if (SearchType.length() > 1)
{
log.warning("Length > 1 - truncated");
SearchType = SearchType.substring(0,1);
}
set_Value ("SearchType", SearchType);
}
/** Get SearchType */
public String getSearchType() 
{
return (String)get_Value("SearchType");
}
/** Set TransactionCode */
public void setTransactionCode (String TransactionCode)
{
if (TransactionCode != null && TransactionCode.length() > 8)
{
log.warning("Length > 8 - truncated");
TransactionCode = TransactionCode.substring(0,8);
}
set_Value ("TransactionCode", TransactionCode);
}
/** Get TransactionCode */
public String getTransactionCode() 
{
return (String)get_Value("TransactionCode");
}
}
