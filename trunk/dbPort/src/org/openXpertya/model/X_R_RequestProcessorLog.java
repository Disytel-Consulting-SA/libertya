/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_RequestProcessorLog
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.187 */
public class X_R_RequestProcessorLog extends PO
{
/** Constructor estándar */
public X_R_RequestProcessorLog (Properties ctx, int R_RequestProcessorLog_ID, String trxName)
{
super (ctx, R_RequestProcessorLog_ID, trxName);
/** if (R_RequestProcessorLog_ID == 0)
{
setIsError (false);
setR_RequestProcessorLog_ID (0);
setR_RequestProcessor_ID (0);
}
 */
}
/** Load Constructor */
public X_R_RequestProcessorLog (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=659 */
public static final int Table_ID=659;

/** TableName=R_RequestProcessorLog */
public static final String Table_Name="R_RequestProcessorLog";

protected static KeyNamePair Model = new KeyNamePair(659,"R_RequestProcessorLog");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_RequestProcessorLog[").append(getID()).append("]");
return sb.toString();
}
/** Set BinaryData.
Binary Data */
public void setBinaryData (byte[] BinaryData)
{
set_Value ("BinaryData", BinaryData);
}
/** Get BinaryData.
Binary Data */
public byte[] getBinaryData() 
{
return (byte[])get_Value("BinaryData");
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
/** Set Error.
An Error occured in the execution */
public void setIsError (boolean IsError)
{
set_Value ("IsError", new Boolean(IsError));
}
/** Get Error.
An Error occured in the execution */
public boolean isError() 
{
Object oo = get_Value("IsError");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Request Processor Log.
Result of the execution of the Request Processor */
public void setR_RequestProcessorLog_ID (int R_RequestProcessorLog_ID)
{
set_ValueNoCheck ("R_RequestProcessorLog_ID", new Integer(R_RequestProcessorLog_ID));
}
/** Get Request Processor Log.
Result of the execution of the Request Processor */
public int getR_RequestProcessorLog_ID() 
{
Integer ii = (Integer)get_Value("R_RequestProcessorLog_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Request Processor.
Processor for Requests */
public void setR_RequestProcessor_ID (int R_RequestProcessor_ID)
{
set_ValueNoCheck ("R_RequestProcessor_ID", new Integer(R_RequestProcessor_ID));
}
/** Get Request Processor.
Processor for Requests */
public int getR_RequestProcessor_ID() 
{
Integer ii = (Integer)get_Value("R_RequestProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Reference.
Reference for this record */
public void setReference (String Reference)
{
if (Reference != null && Reference.length() > 60)
{
log.warning("Length > 60 - truncated");
Reference = Reference.substring(0,59);
}
set_Value ("Reference", Reference);
}
/** Get Reference.
Reference for this record */
public String getReference() 
{
return (String)get_Value("Reference");
}
/** Set Summary.
Textual summary of this request */
public void setSummary (String Summary)
{
if (Summary != null && Summary.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Summary = Summary.substring(0,1999);
}
set_Value ("Summary", Summary);
}
/** Get Summary.
Textual summary of this request */
public String getSummary() 
{
return (String)get_Value("Summary");
}
/** Set Text Message.
Text Message */
public void setTextMsg (String TextMsg)
{
if (TextMsg != null && TextMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
TextMsg = TextMsg.substring(0,1999);
}
set_Value ("TextMsg", TextMsg);
}
/** Get Text Message.
Text Message */
public String getTextMsg() 
{
return (String)get_Value("TextMsg");
}
}
