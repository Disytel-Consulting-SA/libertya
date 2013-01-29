/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DunningRun
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.734 */
public class X_C_DunningRun extends PO
{
/** Constructor estándar */
public X_C_DunningRun (Properties ctx, int C_DunningRun_ID, String trxName)
{
super (ctx, C_DunningRun_ID, trxName);
/** if (C_DunningRun_ID == 0)
{
setC_DunningLevel_ID (0);
setC_DunningRun_ID (0);
setDunningDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_DunningRun (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=526 */
public static final int Table_ID=526;

/** TableName=C_DunningRun */
public static final String Table_Name="C_DunningRun";

protected static KeyNamePair Model = new KeyNamePair(526,"C_DunningRun");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DunningRun[").append(getID()).append("]");
return sb.toString();
}
/** Set Dunning Level */
public void setC_DunningLevel_ID (int C_DunningLevel_ID)
{
set_ValueNoCheck ("C_DunningLevel_ID", new Integer(C_DunningLevel_ID));
}
/** Get Dunning Level */
public int getC_DunningLevel_ID() 
{
Integer ii = (Integer)get_Value("C_DunningLevel_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Dunning Run.
Dunning Run */
public void setC_DunningRun_ID (int C_DunningRun_ID)
{
set_ValueNoCheck ("C_DunningRun_ID", new Integer(C_DunningRun_ID));
}
/** Get Dunning Run.
Dunning Run */
public int getC_DunningRun_ID() 
{
Integer ii = (Integer)get_Value("C_DunningRun_ID");
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
/** Set Dunning Date.
Date of Dunning */
public void setDunningDate (Timestamp DunningDate)
{
if (DunningDate == null) throw new IllegalArgumentException ("DunningDate is mandatory");
set_Value ("DunningDate", DunningDate);
}
/** Get Dunning Date.
Date of Dunning */
public Timestamp getDunningDate() 
{
return (Timestamp)get_Value("DunningDate");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getDunningDate()));
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Send */
public void setSendIt (String SendIt)
{
if (SendIt != null && SendIt.length() > 1)
{
log.warning("Length > 1 - truncated");
SendIt = SendIt.substring(0,0);
}
set_Value ("SendIt", SendIt);
}
/** Get Send */
public String getSendIt() 
{
return (String)get_Value("SendIt");
}
}
