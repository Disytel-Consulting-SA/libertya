/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Record_Access
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.265 */
public class X_AD_Record_Access extends PO
{
/** Constructor estándar */
public X_AD_Record_Access (Properties ctx, int AD_Record_Access_ID, String trxName)
{
super (ctx, AD_Record_Access_ID, trxName);
/** if (AD_Record_Access_ID == 0)
{
setAD_Role_ID (0);
setAD_Table_ID (0);
setIsDependentEntities (false);	// N
setIsExclude (true);	// Y
setIsReadOnly (false);
setRecord_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Record_Access (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=567 */
public static final int Table_ID=567;

/** TableName=AD_Record_Access */
public static final String Table_Name="AD_Record_Access";

protected static KeyNamePair Model = new KeyNamePair(567,"AD_Record_Access");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Record_Access[").append(getID()).append("]");
return sb.toString();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
set_ValueNoCheck ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_ValueNoCheck ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Table_ID()));
}
/** Set Dependent Entities.
Also check access in dependent entities */
public void setIsDependentEntities (boolean IsDependentEntities)
{
set_Value ("IsDependentEntities", new Boolean(IsDependentEntities));
}
/** Get Dependent Entities.
Also check access in dependent entities */
public boolean isDependentEntities() 
{
Object oo = get_Value("IsDependentEntities");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Exclude.
Exclude access to the data - if not selected Include access to the data */
public void setIsExclude (boolean IsExclude)
{
set_Value ("IsExclude", new Boolean(IsExclude));
}
/** Get Exclude.
Exclude access to the data - if not selected Include access to the data */
public boolean isExclude() 
{
Object oo = get_Value("IsExclude");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Read Only.
Field is read only */
public void setIsReadOnly (boolean IsReadOnly)
{
set_Value ("IsReadOnly", new Boolean(IsReadOnly));
}
/** Get Read Only.
Field is read only */
public boolean isReadOnly() 
{
Object oo = get_Value("IsReadOnly");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
set_Value ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
