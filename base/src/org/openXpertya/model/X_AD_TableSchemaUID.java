/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_TableSchemaUID
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-02 14:16:54.637 */
public class X_AD_TableSchemaUID extends PO
{
/** Constructor estándar */
public X_AD_TableSchemaUID (Properties ctx, int AD_TableSchemaUID_ID, String trxName)
{
super (ctx, AD_TableSchemaUID_ID, trxName);
/** if (AD_TableSchemaUID_ID == 0)
{
setAD_Element_ID (0);
setAD_TableSchema_ID (0);
setAD_TableSchemaUID_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_TableSchemaUID (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1010195 */
public static final int Table_ID=1010195;

/** TableName=AD_TableSchemaUID */
public static final String Table_Name="AD_TableSchemaUID";

protected static KeyNamePair Model = new KeyNamePair(1010195,"AD_TableSchemaUID");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_TableSchemaUID[").append(getID()).append("]");
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
/** Set System Element.
System Element enables the central maintenance of column description and help. */
public void setAD_Element_ID (int AD_Element_ID)
{
set_Value ("AD_Element_ID", new Integer(AD_Element_ID));
}
/** Get System Element.
System Element enables the central maintenance of column description and help. */
public int getAD_Element_ID() 
{
Integer ii = (Integer)get_Value("AD_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table Schema.
Table Schema */
public void setAD_TableSchema_ID (int AD_TableSchema_ID)
{
set_Value ("AD_TableSchema_ID", new Integer(AD_TableSchema_ID));
}
/** Get Table Schema.
Table Schema */
public int getAD_TableSchema_ID() 
{
Integer ii = (Integer)get_Value("AD_TableSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table Schema Unique Identifier.
Table Schema Unique Identifier */
public void setAD_TableSchemaUID_ID (int AD_TableSchemaUID_ID)
{
set_ValueNoCheck ("AD_TableSchemaUID_ID", new Integer(AD_TableSchemaUID_ID));
}
/** Get Table Schema Unique Identifier.
Table Schema Unique Identifier */
public int getAD_TableSchemaUID_ID() 
{
Integer ii = (Integer)get_Value("AD_TableSchemaUID_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
