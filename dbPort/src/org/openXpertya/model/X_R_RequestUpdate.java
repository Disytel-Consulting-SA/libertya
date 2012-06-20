/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_RequestUpdate
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.296 */
public class X_R_RequestUpdate extends PO
{
/** Constructor estándar */
public X_R_RequestUpdate (Properties ctx, int R_RequestUpdate_ID, String trxName)
{
super (ctx, R_RequestUpdate_ID, trxName);
/** if (R_RequestUpdate_ID == 0)
{
setConfidentialTypeEntry (null);
setR_RequestUpdate_ID (0);
setR_Request_ID (0);
}
 */
}
/** Load Constructor */
public X_R_RequestUpdate (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=802 */
public static final int Table_ID=802;

/** TableName=R_RequestUpdate */
public static final String Table_Name="R_RequestUpdate";

protected static KeyNamePair Model = new KeyNamePair(802,"R_RequestUpdate");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_RequestUpdate[").append(getID()).append("]");
return sb.toString();
}
public static final int CONFIDENTIALTYPEENTRY_AD_Reference_ID=340;
/** Public Information = A */
public static final String CONFIDENTIALTYPEENTRY_PublicInformation = "A";
/** Private Information = P */
public static final String CONFIDENTIALTYPEENTRY_PrivateInformation = "P";
/** Customer Confidential = C */
public static final String CONFIDENTIALTYPEENTRY_CustomerConfidential = "C";
/** Internal = I */
public static final String CONFIDENTIALTYPEENTRY_Internal = "I";
/** Set Entry Confidentiality.
Confidentiality of the individual entry */
public void setConfidentialTypeEntry (String ConfidentialTypeEntry)
{
if (ConfidentialTypeEntry.equals("A") || ConfidentialTypeEntry.equals("P") || ConfidentialTypeEntry.equals("C") || ConfidentialTypeEntry.equals("I"));
 else throw new IllegalArgumentException ("ConfidentialTypeEntry Invalid value - Reference_ID=340 - A - P - C - I");
if (ConfidentialTypeEntry == null) throw new IllegalArgumentException ("ConfidentialTypeEntry is mandatory");
if (ConfidentialTypeEntry.length() > 1)
{
log.warning("Length > 1 - truncated");
ConfidentialTypeEntry = ConfidentialTypeEntry.substring(0,0);
}
set_Value ("ConfidentialTypeEntry", ConfidentialTypeEntry);
}
/** Get Entry Confidentiality.
Confidentiality of the individual entry */
public String getConfidentialTypeEntry() 
{
return (String)get_Value("ConfidentialTypeEntry");
}
/** Set End Time.
End of the time span */
public void setEndTime (Timestamp EndTime)
{
set_Value ("EndTime", EndTime);
}
/** Get End Time.
End of the time span */
public Timestamp getEndTime() 
{
return (Timestamp)get_Value("EndTime");
}
public static final int M_PRODUCTSPENT_ID_AD_Reference_ID=162;
/** Set Product Used.
Product/Resource/Service used in Request */
public void setM_ProductSpent_ID (int M_ProductSpent_ID)
{
if (M_ProductSpent_ID <= 0) set_Value ("M_ProductSpent_ID", null);
 else 
set_Value ("M_ProductSpent_ID", new Integer(M_ProductSpent_ID));
}
/** Get Product Used.
Product/Resource/Service used in Request */
public int getM_ProductSpent_ID() 
{
Integer ii = (Integer)get_Value("M_ProductSpent_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
set_Value ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity Used.
Quantity used for this event */
public void setQtySpent (BigDecimal QtySpent)
{
set_Value ("QtySpent", QtySpent);
}
/** Get Quantity Used.
Quantity used for this event */
public BigDecimal getQtySpent() 
{
BigDecimal bd = (BigDecimal)get_Value("QtySpent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Request Update.
Request Updates */
public void setR_RequestUpdate_ID (int R_RequestUpdate_ID)
{
set_ValueNoCheck ("R_RequestUpdate_ID", new Integer(R_RequestUpdate_ID));
}
/** Get Request Update.
Request Updates */
public int getR_RequestUpdate_ID() 
{
Integer ii = (Integer)get_Value("R_RequestUpdate_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getR_RequestUpdate_ID()));
}
/** Set Request.
Request from a Business Partner or Prospect */
public void setR_Request_ID (int R_Request_ID)
{
set_ValueNoCheck ("R_Request_ID", new Integer(R_Request_ID));
}
/** Get Request.
Request from a Business Partner or Prospect */
public int getR_Request_ID() 
{
Integer ii = (Integer)get_Value("R_Request_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Result.
Result of the action taken */
public void setResult (String Result)
{
if (Result != null && Result.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Result = Result.substring(0,1999);
}
set_ValueNoCheck ("Result", Result);
}
/** Get Result.
Result of the action taken */
public String getResult() 
{
return (String)get_Value("Result");
}
/** Set Start Time.
Time started */
public void setStartTime (Timestamp StartTime)
{
set_Value ("StartTime", StartTime);
}
/** Get Start Time.
Time started */
public Timestamp getStartTime() 
{
return (Timestamp)get_Value("StartTime");
}
}
