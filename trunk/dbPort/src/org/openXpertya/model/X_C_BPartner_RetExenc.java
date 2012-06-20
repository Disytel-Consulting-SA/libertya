/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_RetExenc
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.312 */
public class X_C_BPartner_RetExenc extends PO
{
/** Constructor estándar */
public X_C_BPartner_RetExenc (Properties ctx, int C_BPartner_RetExenc_ID, String trxName)
{
super (ctx, C_BPartner_RetExenc_ID, trxName);
/** if (C_BPartner_RetExenc_ID == 0)
{
setC_BPartner_RetExenc_ID (0);
setC_BPartner_Retencion_ID (0);
setdate_from (new Timestamp(System.currentTimeMillis()));
setdate_to (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_BPartner_RetExenc (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000143 */
public static final int Table_ID=1000143;

/** TableName=C_BPartner_RetExenc */
public static final String Table_Name="C_BPartner_RetExenc";

protected static KeyNamePair Model = new KeyNamePair(1000143,"C_BPartner_RetExenc");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_RetExenc[").append(getID()).append("]");
return sb.toString();
}
/** Set Exception Retencion */
public void setC_BPartner_RetExenc_ID (int C_BPartner_RetExenc_ID)
{
set_ValueNoCheck ("C_BPartner_RetExenc_ID", new Integer(C_BPartner_RetExenc_ID));
}
/** Get Exception Retencion */
public int getC_BPartner_RetExenc_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_RetExenc_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Vendor Retencion */
public void setC_BPartner_Retencion_ID (int C_BPartner_Retencion_ID)
{
set_Value ("C_BPartner_Retencion_ID", new Integer(C_BPartner_Retencion_ID));
}
/** Get Vendor Retencion */
public int getC_BPartner_Retencion_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Retencion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,59);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Percent.
Percentage */
public void setPercent (BigDecimal Percent)
{
set_Value ("Percent", Percent);
}
/** Get Percent.
Percentage */
public BigDecimal getPercent() 
{
BigDecimal bd = (BigDecimal)get_Value("Percent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set From Date */
public void setdate_from (Timestamp date_from)
{
if (date_from == null) throw new IllegalArgumentException ("date_from is mandatory");
set_Value ("date_from", date_from);
}
/** Get From Date */
public Timestamp getdate_from() 
{
return (Timestamp)get_Value("date_from");
}
/** Set To Date */
public void setdate_to (Timestamp date_to)
{
if (date_to == null) throw new IllegalArgumentException ("date_to is mandatory");
set_Value ("date_to", date_to);
}
/** Get To Date */
public Timestamp getdate_to() 
{
return (Timestamp)get_Value("date_to");
}
}
