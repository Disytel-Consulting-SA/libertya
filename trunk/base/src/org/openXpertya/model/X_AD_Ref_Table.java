/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Ref_Table
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-06-18 17:45:05.244 */
public class X_AD_Ref_Table extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Ref_Table (Properties ctx, int AD_Ref_Table_ID, String trxName)
{
super (ctx, AD_Ref_Table_ID, trxName);
/** if (AD_Ref_Table_ID == 0)
{
setAD_Display (0);
setAD_Key (0);
setAD_Reference_ID (0);
setAD_Table_ID (0);
setEntityType (null);	// U
setIsDisplayIdentifiers (false);
setIsValueDisplayed (false);
}
 */
}
/** Load Constructor */
public X_AD_Ref_Table (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Ref_Table");

/** TableName=AD_Ref_Table */
public static final String Table_Name="AD_Ref_Table";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Ref_Table");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Ref_Table[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_DISPLAY_AD_Reference_ID = MReference.getReferenceID("AD_Column ColumName");
/** Set Display column.
Column that will display */
public void setAD_Display (int AD_Display)
{
set_Value ("AD_Display", new Integer(AD_Display));
}
/** Get Display column.
Column that will display */
public int getAD_Display() 
{
Integer ii = (Integer)get_Value("AD_Display");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_KEY_AD_Reference_ID = MReference.getReferenceID("AD_Column ColumName");
/** Set Key column.
Unique identifier of a record */
public void setAD_Key (int AD_Key)
{
set_Value ("AD_Key", new Integer(AD_Key));
}
/** Get Key column.
Unique identifier of a record */
public int getAD_Key() 
{
Integer ii = (Integer)get_Value("AD_Key");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Reference.
System Reference (Pick List) */
public void setAD_Reference_ID (int AD_Reference_ID)
{
set_ValueNoCheck ("AD_Reference_ID", new Integer(AD_Reference_ID));
}
/** Get Reference.
System Reference (Pick List) */
public int getAD_Reference_ID() 
{
Integer ii = (Integer)get_Value("AD_Reference_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Reference_ID()));
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
public static final int ENTITYTYPE_AD_Reference_ID = MReference.getReferenceID("_Entity Type");
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference = ENTITYTYPE_AD_Reference_ID - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
}
/** Set Is Display Identifiers.
The showing value is the value of concatenated columns marked as identifiers */
public void setIsDisplayIdentifiers (boolean IsDisplayIdentifiers)
{
set_Value ("IsDisplayIdentifiers", new Boolean(IsDisplayIdentifiers));
}
/** Get Is Display Identifiers.
The showing value is the value of concatenated columns marked as identifiers */
public boolean isDisplayIdentifiers() 
{
Object oo = get_Value("IsDisplayIdentifiers");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Display Value.
Displays Value column with the Display column */
public void setIsValueDisplayed (boolean IsValueDisplayed)
{
set_Value ("IsValueDisplayed", new Boolean(IsValueDisplayed));
}
/** Get Display Value.
Displays Value column with the Display column */
public boolean isValueDisplayed() 
{
Object oo = get_Value("IsValueDisplayed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sql ORDER BY.
Fully qualified ORDER BY clause */
public void setOrderByClause (String OrderByClause)
{
if (OrderByClause != null && OrderByClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
OrderByClause = OrderByClause.substring(0,2000);
}
set_Value ("OrderByClause", OrderByClause);
}
/** Get Sql ORDER BY.
Fully qualified ORDER BY clause */
public String getOrderByClause() 
{
return (String)get_Value("OrderByClause");
}
/** Set Sql WHERE.
Fully qualified SQL WHERE clause */
public void setWhereClause (String WhereClause)
{
if (WhereClause != null && WhereClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WhereClause = WhereClause.substring(0,2000);
}
set_Value ("WhereClause", WhereClause);
}
/** Get Sql WHERE.
Fully qualified SQL WHERE clause */
public String getWhereClause() 
{
return (String)get_Value("WhereClause");
}
}
