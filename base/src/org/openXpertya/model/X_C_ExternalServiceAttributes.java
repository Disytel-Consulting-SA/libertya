/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ExternalServiceAttributes
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-05-08 11:04:30.871 */
public class X_C_ExternalServiceAttributes extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_ExternalServiceAttributes (Properties ctx, int C_ExternalServiceAttributes_ID, String trxName)
{
super (ctx, C_ExternalServiceAttributes_ID, trxName);
/** if (C_ExternalServiceAttributes_ID == 0)
{
setC_Externalserviceattributes_ID (0);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_ExternalServiceAttributes (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_ExternalServiceAttributes");

/** TableName=C_ExternalServiceAttributes */
public static final String Table_Name="C_ExternalServiceAttributes";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_ExternalServiceAttributes");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ExternalServiceAttributes[").append(getID()).append("]");
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
/** Set C_Externalserviceattributes_ID */
public void setC_Externalserviceattributes_ID (int C_Externalserviceattributes_ID)
{
set_ValueNoCheck ("C_Externalserviceattributes_ID", new Integer(C_Externalserviceattributes_ID));
}
/** Get C_Externalserviceattributes_ID */
public int getC_Externalserviceattributes_ID() 
{
Integer ii = (Integer)get_Value("C_Externalserviceattributes_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set External Service */
public void setC_ExternalService_ID (int C_ExternalService_ID)
{
if (C_ExternalService_ID <= 0) set_Value ("C_ExternalService_ID", null);
 else 
set_Value ("C_ExternalService_ID", new Integer(C_ExternalService_ID));
}
/** Get External Service */
public int getC_ExternalService_ID() 
{
Integer ii = (Integer)get_Value("C_ExternalService_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 128)
{
log.warning("Length > 128 - truncated");
Name = Name.substring(0,128);
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 128)
{
log.warning("Length > 128 - truncated");
Value = Value.substring(0,128);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
