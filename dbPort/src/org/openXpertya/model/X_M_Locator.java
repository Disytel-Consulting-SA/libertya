/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Locator
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:35.801 */
public class X_M_Locator extends PO
{
/** Constructor estándar */
public X_M_Locator (Properties ctx, int M_Locator_ID, String trxName)
{
super (ctx, M_Locator_ID, trxName);
/** if (M_Locator_ID == 0)
{
setIsDefault (false);
setM_Locator_ID (0);
setM_Warehouse_ID (0);
setPriorityNo (0);	// 50
setValue (null);
setX (null);
setY (null);
setZ (null);
}
 */
}
/** Load Constructor */
public X_M_Locator (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=207 */
public static final int Table_ID=207;

/** TableName=M_Locator */
public static final String Table_Name="M_Locator";

protected static KeyNamePair Model = new KeyNamePair(207,"M_Locator");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Locator[").append(getID()).append("]");
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
/** Set Depth */
public void setDepth (BigDecimal Depth)
{
set_Value ("Depth", Depth);
}
/** Get Depth */
public BigDecimal getDepth() 
{
BigDecimal bd = (BigDecimal)get_Value("Depth");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Height */
public void setHeight (BigDecimal Height)
{
set_Value ("Height", Height);
}
/** Get Height */
public BigDecimal getHeight() 
{
BigDecimal bd = (BigDecimal)get_Value("Height");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
set_ValueNoCheck ("M_Locator_ID", new Integer(M_Locator_ID));
}
/** Get Locator.
Warehouse Locator */
public int getM_Locator_ID() 
{
Integer ii = (Integer)get_Value("M_Locator_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_ValueNoCheck ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Relative Priority.
Where inventory should be picked from first */
public void setPriorityNo (int PriorityNo)
{
set_Value ("PriorityNo", new Integer(PriorityNo));
}
/** Get Relative Priority.
Where inventory should be picked from first */
public int getPriorityNo() 
{
Integer ii = (Integer)get_Value("PriorityNo");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
/** Set Width */
public void setWidth (BigDecimal Width)
{
set_Value ("Width", Width);
}
/** Get Width */
public BigDecimal getWidth() 
{
BigDecimal bd = (BigDecimal)get_Value("Width");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Aisle (X).
X dimension, e.g., Aisle */
public void setX (String X)
{
if (X == null) throw new IllegalArgumentException ("X is mandatory");
if (X.length() > 60)
{
log.warning("Length > 60 - truncated");
X = X.substring(0,60);
}
set_Value ("X", X);
}
/** Get Aisle (X).
X dimension, e.g., Aisle */
public String getX() 
{
return (String)get_Value("X");
}
/** Set Bin (Y).
Y dimension, e.g., Bin */
public void setY (String Y)
{
if (Y == null) throw new IllegalArgumentException ("Y is mandatory");
if (Y.length() > 60)
{
log.warning("Length > 60 - truncated");
Y = Y.substring(0,60);
}
set_Value ("Y", Y);
}
/** Get Bin (Y).
Y dimension, e.g., Bin */
public String getY() 
{
return (String)get_Value("Y");
}
/** Set Level (Z).
Z dimension, e.g., Level */
public void setZ (String Z)
{
if (Z == null) throw new IllegalArgumentException ("Z is mandatory");
if (Z.length() > 60)
{
log.warning("Length > 60 - truncated");
Z = Z.substring(0,60);
}
set_Value ("Z", Z);
}
/** Get Level (Z).
Z dimension, e.g., Level */
public String getZ() 
{
return (String)get_Value("Z");
}
}
