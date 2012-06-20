/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_PackageLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.75 */
public class X_M_PackageLine extends PO
{
/** Constructor estándar */
public X_M_PackageLine (Properties ctx, int M_PackageLine_ID, String trxName)
{
super (ctx, M_PackageLine_ID, trxName);
/** if (M_PackageLine_ID == 0)
{
setM_InOutLine_ID (0);
setM_PackageLine_ID (0);
setM_Package_ID (0);
setQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_PackageLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=663 */
public static final int Table_ID=663;

/** TableName=M_PackageLine */
public static final String Table_Name="M_PackageLine";

protected static KeyNamePair Model = new KeyNamePair(663,"M_PackageLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_PackageLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
set_ValueNoCheck ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Package Line.
The detail content of the Package */
public void setM_PackageLine_ID (int M_PackageLine_ID)
{
set_ValueNoCheck ("M_PackageLine_ID", new Integer(M_PackageLine_ID));
}
/** Get Package Line.
The detail content of the Package */
public int getM_PackageLine_ID() 
{
Integer ii = (Integer)get_Value("M_PackageLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Package.
Shipment Package */
public void setM_Package_ID (int M_Package_ID)
{
set_ValueNoCheck ("M_Package_ID", new Integer(M_Package_ID));
}
/** Get Package.
Shipment Package */
public int getM_Package_ID() 
{
Integer ii = (Integer)get_Value("M_Package_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_Package_ID()));
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
if (Qty == null) throw new IllegalArgumentException ("Qty is mandatory");
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
}
