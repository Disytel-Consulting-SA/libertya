/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_RetExenc
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-05-17 17:47:27.307 */
public class X_C_BPartner_RetExenc extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_RetExenc (Properties ctx, int C_BPartner_RetExenc_ID, String trxName)
{
super (ctx, C_BPartner_RetExenc_ID, trxName);
/** if (C_BPartner_RetExenc_ID == 0)
{
setC_BPartner_Retencion_ID (0);
setC_BPartner_RetExenc_ID (0);
setDate_From (new Timestamp(System.currentTimeMillis()));
setDate_To (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_BPartner_RetExenc (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_RetExenc");

/** TableName=C_BPartner_RetExenc */
public static final String Table_Name="C_BPartner_RetExenc";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_RetExenc");
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
/** Set Vendor Retencion */
public void setC_BPartner_Retencion_ID (int C_BPartner_Retencion_ID)
{
set_ValueNoCheck ("C_BPartner_Retencion_ID", new Integer(C_BPartner_Retencion_ID));
}
/** Get Vendor Retencion */
public int getC_BPartner_Retencion_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Retencion_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set From Date */
public void setDate_From (Timestamp Date_From)
{
if (Date_From == null) throw new IllegalArgumentException ("Date_From is mandatory");
set_Value ("Date_From", Date_From);
}
/** Get From Date */
public Timestamp getDate_From() 
{
return (Timestamp)get_Value("Date_From");
}
/** Set To Date */
public void setDate_To (Timestamp Date_To)
{
if (Date_To == null) throw new IllegalArgumentException ("Date_To is mandatory");
set_Value ("Date_To", Date_To);
}
/** Get To Date */
public Timestamp getDate_To() 
{
return (Timestamp)get_Value("Date_To");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,60);
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
}
