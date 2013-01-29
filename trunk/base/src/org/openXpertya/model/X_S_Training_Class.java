/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por S_Training_Class
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.703 */
public class X_S_Training_Class extends PO
{
/** Constructor estándar */
public X_S_Training_Class (Properties ctx, int S_Training_Class_ID, String trxName)
{
super (ctx, S_Training_Class_ID, trxName);
/** if (S_Training_Class_ID == 0)
{
setEndDate (new Timestamp(System.currentTimeMillis()));
setM_Product_ID (0);
setS_Training_Class_ID (0);
setS_Training_ID (0);
setStartDate (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_S_Training_Class (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=537 */
public static final int Table_ID=537;

/** TableName=S_Training_Class */
public static final String Table_Name="S_Training_Class";

protected static KeyNamePair Model = new KeyNamePair(537,"S_Training_Class");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_S_Training_Class[").append(getID()).append("]");
return sb.toString();
}
/** Set End Date.
Last effective date (inclusive) */
public void setEndDate (Timestamp EndDate)
{
if (EndDate == null) throw new IllegalArgumentException ("EndDate is mandatory");
set_Value ("EndDate", EndDate);
}
/** Get End Date.
Last effective date (inclusive) */
public Timestamp getEndDate() 
{
return (Timestamp)get_Value("EndDate");
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Training Class.
The actual training class instance */
public void setS_Training_Class_ID (int S_Training_Class_ID)
{
set_ValueNoCheck ("S_Training_Class_ID", new Integer(S_Training_Class_ID));
}
/** Get Training Class.
The actual training class instance */
public int getS_Training_Class_ID() 
{
Integer ii = (Integer)get_Value("S_Training_Class_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Training.
Repeated Training */
public void setS_Training_ID (int S_Training_ID)
{
set_ValueNoCheck ("S_Training_ID", new Integer(S_Training_ID));
}
/** Get Training.
Repeated Training */
public int getS_Training_ID() 
{
Integer ii = (Integer)get_Value("S_Training_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Start Date.
First effective day (inclusive) */
public void setStartDate (Timestamp StartDate)
{
if (StartDate == null) throw new IllegalArgumentException ("StartDate is mandatory");
set_Value ("StartDate", StartDate);
}
/** Get Start Date.
First effective day (inclusive) */
public Timestamp getStartDate() 
{
return (Timestamp)get_Value("StartDate");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getStartDate()));
}
}
