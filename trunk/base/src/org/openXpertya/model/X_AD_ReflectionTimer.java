/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ReflectionTimer
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-09-20 15:31:31.127 */
public class X_AD_ReflectionTimer extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ReflectionTimer (Properties ctx, int AD_ReflectionTimer_ID, String trxName)
{
super (ctx, AD_ReflectionTimer_ID, trxName);
/** if (AD_ReflectionTimer_ID == 0)
{
setAD_ReflectionTimer_ID (0);
setName (null);
setTimeout (0);
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_ReflectionTimer (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ReflectionTimer");

/** TableName=AD_ReflectionTimer */
public static final String Table_Name="AD_ReflectionTimer";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ReflectionTimer");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ReflectionTimer[").append(getID()).append("]");
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
/** Set AD_ReflectionTimer_ID */
public void setAD_ReflectionTimer_ID (int AD_ReflectionTimer_ID)
{
set_ValueNoCheck ("AD_ReflectionTimer_ID", new Integer(AD_ReflectionTimer_ID));
}
/** Get AD_ReflectionTimer_ID */
public int getAD_ReflectionTimer_ID() 
{
Integer ii = (Integer)get_Value("AD_ReflectionTimer_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
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
/** Set Timeout */
public void setTimeout (int Timeout)
{
set_Value ("Timeout", new Integer(Timeout));
}
/** Get Timeout */
public int getTimeout() 
{
Integer ii = (Integer)get_Value("Timeout");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
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
