/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PInstance
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:23.437 */
public class X_AD_PInstance extends PO
{
/** Constructor estándar */
public X_AD_PInstance (Properties ctx, int AD_PInstance_ID, String trxName)
{
super (ctx, AD_PInstance_ID, trxName);
/** if (AD_PInstance_ID == 0)
{
setAD_PInstance_ID (0);
setAD_Process_ID (0);
setIsProcessing (false);
setRecord_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_PInstance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=282 */
public static final int Table_ID=282;

/** TableName=AD_PInstance */
public static final String Table_Name="AD_PInstance";

protected static KeyNamePair Model = new KeyNamePair(282,"AD_PInstance");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PInstance[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_ValueNoCheck ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_PInstance_ID()));
}
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
set_Value ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Error Msg */
public void setErrorMsg (String ErrorMsg)
{
if (ErrorMsg != null && ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
ErrorMsg = ErrorMsg.substring(0,1999);
}
set_Value ("ErrorMsg", ErrorMsg);
}
/** Get Error Msg */
public String getErrorMsg() 
{
return (String)get_Value("ErrorMsg");
}
/** Set Processing */
public void setIsProcessing (boolean IsProcessing)
{
set_Value ("IsProcessing", new Boolean(IsProcessing));
}
/** Get Processing */
public boolean isProcessing() 
{
Object oo = get_Value("IsProcessing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
set_ValueNoCheck ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Result.
Result of the action taken */
public void setResult (int Result)
{
set_Value ("Result", new Integer(Result));
}
/** Get Result.
Result of the action taken */
public int getResult() 
{
Integer ii = (Integer)get_Value("Result");
if (ii == null) return 0;
return ii.intValue();
}
}
