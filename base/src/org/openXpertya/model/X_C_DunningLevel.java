/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DunningLevel
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.703 */
public class X_C_DunningLevel extends PO
{
/** Constructor estándar */
public X_C_DunningLevel (Properties ctx, int C_DunningLevel_ID, String trxName)
{
super (ctx, C_DunningLevel_ID, trxName);
/** if (C_DunningLevel_ID == 0)
{
setC_DunningLevel_ID (0);
setC_Dunning_ID (0);
setChargeFee (false);
setChargeInterest (false);
setDaysAfterDue (Env.ZERO);
setDaysBetweenDunning (0);
setName (null);
setPrintName (null);
}
 */
}
/** Load Constructor */
public X_C_DunningLevel (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=331 */
public static final int Table_ID=331;

/** TableName=C_DunningLevel */
public static final String Table_Name="C_DunningLevel";

protected static KeyNamePair Model = new KeyNamePair(331,"C_DunningLevel");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DunningLevel[").append(getID()).append("]");
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
/** Set Dunning.
Dunning Rules for overdue invoices */
public void setC_Dunning_ID (int C_Dunning_ID)
{
set_ValueNoCheck ("C_Dunning_ID", new Integer(C_Dunning_ID));
}
/** Get Dunning.
Dunning Rules for overdue invoices */
public int getC_Dunning_ID() 
{
Integer ii = (Integer)get_Value("C_Dunning_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charge fee.
Indicates if fees will be charged for overdue invoices */
public void setChargeFee (boolean ChargeFee)
{
set_Value ("ChargeFee", new Boolean(ChargeFee));
}
/** Get Charge fee.
Indicates if fees will be charged for overdue invoices */
public boolean isChargeFee() 
{
Object oo = get_Value("ChargeFee");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Charge Interest.
Indicates if interest will be charged on overdue invoices */
public void setChargeInterest (boolean ChargeInterest)
{
set_Value ("ChargeInterest", new Boolean(ChargeInterest));
}
/** Get Charge Interest.
Indicates if interest will be charged on overdue invoices */
public boolean isChargeInterest() 
{
Object oo = get_Value("ChargeInterest");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Days after due date.
Days after due date to dun (if negative days until due) */
public void setDaysAfterDue (BigDecimal DaysAfterDue)
{
if (DaysAfterDue == null) throw new IllegalArgumentException ("DaysAfterDue is mandatory");
set_Value ("DaysAfterDue", DaysAfterDue);
}
/** Get Days after due date.
Days after due date to dun (if negative days until due) */
public BigDecimal getDaysAfterDue() 
{
BigDecimal bd = (BigDecimal)get_Value("DaysAfterDue");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Days between dunning.
Days between sending dunning notices */
public void setDaysBetweenDunning (int DaysBetweenDunning)
{
set_Value ("DaysBetweenDunning", new Integer(DaysBetweenDunning));
}
/** Get Days between dunning.
Days between sending dunning notices */
public int getDaysBetweenDunning() 
{
Integer ii = (Integer)get_Value("DaysBetweenDunning");
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
public static final int DUNNING_PRINTFORMAT_ID_AD_Reference_ID=259;
/** Set Dunning Print Format.
Print Format for printing Dunning Letters */
public void setDunning_PrintFormat_ID (int Dunning_PrintFormat_ID)
{
if (Dunning_PrintFormat_ID <= 0) set_Value ("Dunning_PrintFormat_ID", null);
 else 
set_Value ("Dunning_PrintFormat_ID", new Integer(Dunning_PrintFormat_ID));
}
/** Get Dunning Print Format.
Print Format for printing Dunning Letters */
public int getDunning_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Dunning_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fee Amount.
Fee amount in invoice currency */
public void setFeeAmt (BigDecimal FeeAmt)
{
set_Value ("FeeAmt", FeeAmt);
}
/** Get Fee Amount.
Fee amount in invoice currency */
public BigDecimal getFeeAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FeeAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Interest in percent.
Percentage interest to charge on overdue invoices */
public void setInterestPercent (BigDecimal InterestPercent)
{
set_Value ("InterestPercent", InterestPercent);
}
/** Get Interest in percent.
Percentage interest to charge on overdue invoices */
public BigDecimal getInterestPercent() 
{
BigDecimal bd = (BigDecimal)get_Value("InterestPercent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Note.
Optional additional user defined information */
public void setNote (String Note)
{
if (Note != null && Note.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Note = Note.substring(0,1999);
}
set_Value ("Note", Note);
}
/** Get Note.
Optional additional user defined information */
public String getNote() 
{
return (String)get_Value("Note");
}
/** Set Print Text.
The label text to be printed on a document or correspondence. */
public void setPrintName (String PrintName)
{
if (PrintName == null) throw new IllegalArgumentException ("PrintName is mandatory");
if (PrintName.length() > 60)
{
log.warning("Length > 60 - truncated");
PrintName = PrintName.substring(0,59);
}
set_Value ("PrintName", PrintName);
}
/** Get Print Text.
The label text to be printed on a document or correspondence. */
public String getPrintName() 
{
return (String)get_Value("PrintName");
}
}
