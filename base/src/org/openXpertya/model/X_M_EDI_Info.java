/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_EDI_Info
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.75 */
public class X_M_EDI_Info extends PO
{
/** Constructor estándar */
public X_M_EDI_Info (Properties ctx, int M_EDI_Info_ID, String trxName)
{
super (ctx, M_EDI_Info_ID, trxName);
/** if (M_EDI_Info_ID == 0)
{
setInfo (null);
setM_EDI_ID (0);
setM_EDI_Info_ID (0);
}
 */
}
/** Load Constructor */
public X_M_EDI_Info (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=368 */
public static final int Table_ID=368;

/** TableName=M_EDI_Info */
public static final String Table_Name="M_EDI_Info";

protected static KeyNamePair Model = new KeyNamePair(368,"M_EDI_Info");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_EDI_Info[").append(getID()).append("]");
return sb.toString();
}
/** Set Info.
Information */
public void setInfo (String Info)
{
if (Info == null) throw new IllegalArgumentException ("Info is mandatory");
if (Info.length() > 4000)
{
log.warning("Length > 4000 - truncated");
Info = Info.substring(0,3999);
}
set_Value ("Info", Info);
}
/** Get Info.
Information */
public String getInfo() 
{
return (String)get_Value("Info");
}
/** Set EDI Transaction */
public void setM_EDI_ID (int M_EDI_ID)
{
set_ValueNoCheck ("M_EDI_ID", new Integer(M_EDI_ID));
}
/** Get EDI Transaction */
public int getM_EDI_ID() 
{
Integer ii = (Integer)get_Value("M_EDI_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_EDI_ID()));
}
/** Set EDI Log */
public void setM_EDI_Info_ID (int M_EDI_Info_ID)
{
set_ValueNoCheck ("M_EDI_Info_ID", new Integer(M_EDI_Info_ID));
}
/** Get EDI Log */
public int getM_EDI_Info_ID() 
{
Integer ii = (Integer)get_Value("M_EDI_Info_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
