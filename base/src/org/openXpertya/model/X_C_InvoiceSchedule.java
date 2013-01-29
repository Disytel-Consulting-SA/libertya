/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_InvoiceSchedule
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:30.265 */
public class X_C_InvoiceSchedule extends PO
{
/** Constructor estándar */
public X_C_InvoiceSchedule (Properties ctx, int C_InvoiceSchedule_ID, String trxName)
{
super (ctx, C_InvoiceSchedule_ID, trxName);
/** if (C_InvoiceSchedule_ID == 0)
{
setAmt (Env.ZERO);
setC_InvoiceSchedule_ID (0);
setInvoiceDay (0);	// 1
setInvoiceFrequency (null);
setInvoiceWeekDay (null);
setIsAmount (false);
setIsDefault (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_InvoiceSchedule (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=257 */
public static final int Table_ID=257;

/** TableName=C_InvoiceSchedule */
public static final String Table_Name="C_InvoiceSchedule";

protected static KeyNamePair Model = new KeyNamePair(257,"C_InvoiceSchedule");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_InvoiceSchedule[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount */
public void setAmt (BigDecimal Amt)
{
if (Amt == null) throw new IllegalArgumentException ("Amt is mandatory");
set_Value ("Amt", Amt);
}
/** Get Amount.
Amount */
public BigDecimal getAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoice Schedule.
Schedule for generating Invoices */
public void setC_InvoiceSchedule_ID (int C_InvoiceSchedule_ID)
{
set_ValueNoCheck ("C_InvoiceSchedule_ID", new Integer(C_InvoiceSchedule_ID));
}
/** Get Invoice Schedule.
Schedule for generating Invoices */
public int getC_InvoiceSchedule_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceSchedule_ID");
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
/** Set Invoice on even weeks.
Send invoices on even weeks */
public void setEvenInvoiceWeek (boolean EvenInvoiceWeek)
{
set_Value ("EvenInvoiceWeek", new Boolean(EvenInvoiceWeek));
}
/** Get Invoice on even weeks.
Send invoices on even weeks */
public boolean isEvenInvoiceWeek() 
{
Object oo = get_Value("EvenInvoiceWeek");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Invoice Day.
Day of Invoice Generation */
public void setInvoiceDay (int InvoiceDay)
{
set_Value ("InvoiceDay", new Integer(InvoiceDay));
}
/** Get Invoice Day.
Day of Invoice Generation */
public int getInvoiceDay() 
{
Integer ii = (Integer)get_Value("InvoiceDay");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice day cut-off.
Last day for including shipments */
public void setInvoiceDayCutoff (int InvoiceDayCutoff)
{
set_Value ("InvoiceDayCutoff", new Integer(InvoiceDayCutoff));
}
/** Get Invoice day cut-off.
Last day for including shipments */
public int getInvoiceDayCutoff() 
{
Integer ii = (Integer)get_Value("InvoiceDayCutoff");
if (ii == null) return 0;
return ii.intValue();
}
public static final int INVOICEFREQUENCY_AD_Reference_ID=168;
/** Daily = D */
public static final String INVOICEFREQUENCY_Daily = "D";
/** Weekly = W */
public static final String INVOICEFREQUENCY_Weekly = "W";
/** Monthly = M */
public static final String INVOICEFREQUENCY_Monthly = "M";
/** Twice Monthly = T */
public static final String INVOICEFREQUENCY_TwiceMonthly = "T";
/** Set Invoice Frequency.
How often invoices will be generated */
public void setInvoiceFrequency (String InvoiceFrequency)
{
if (InvoiceFrequency.equals("D") || InvoiceFrequency.equals("W") || InvoiceFrequency.equals("M") || InvoiceFrequency.equals("T"));
 else throw new IllegalArgumentException ("InvoiceFrequency Invalid value - Reference_ID=168 - D - W - M - T");
if (InvoiceFrequency == null) throw new IllegalArgumentException ("InvoiceFrequency is mandatory");
if (InvoiceFrequency.length() > 1)
{
log.warning("Length > 1 - truncated");
InvoiceFrequency = InvoiceFrequency.substring(0,0);
}
set_Value ("InvoiceFrequency", InvoiceFrequency);
}
/** Get Invoice Frequency.
How often invoices will be generated */
public String getInvoiceFrequency() 
{
return (String)get_Value("InvoiceFrequency");
}
public static final int INVOICEWEEKDAY_AD_Reference_ID=167;
/** Sunday = 7 */
public static final String INVOICEWEEKDAY_Sunday = "7";
/** Monday = 1 */
public static final String INVOICEWEEKDAY_Monday = "1";
/** Tuesday = 2 */
public static final String INVOICEWEEKDAY_Tuesday = "2";
/** Wednesday = 3 */
public static final String INVOICEWEEKDAY_Wednesday = "3";
/** Thursday = 4 */
public static final String INVOICEWEEKDAY_Thursday = "4";
/** Friday = 5 */
public static final String INVOICEWEEKDAY_Friday = "5";
/** Saturday = 6 */
public static final String INVOICEWEEKDAY_Saturday = "6";
/** Set Invoice Week Day.
Day to generate invoices */
public void setInvoiceWeekDay (String InvoiceWeekDay)
{
if (InvoiceWeekDay.equals("7") || InvoiceWeekDay.equals("1") || InvoiceWeekDay.equals("2") || InvoiceWeekDay.equals("3") || InvoiceWeekDay.equals("4") || InvoiceWeekDay.equals("5") || InvoiceWeekDay.equals("6"));
 else throw new IllegalArgumentException ("InvoiceWeekDay Invalid value - Reference_ID=167 - 7 - 1 - 2 - 3 - 4 - 5 - 6");
if (InvoiceWeekDay == null) throw new IllegalArgumentException ("InvoiceWeekDay is mandatory");
if (InvoiceWeekDay.length() > 1)
{
log.warning("Length > 1 - truncated");
InvoiceWeekDay = InvoiceWeekDay.substring(0,0);
}
set_Value ("InvoiceWeekDay", InvoiceWeekDay);
}
/** Get Invoice Week Day.
Day to generate invoices */
public String getInvoiceWeekDay() 
{
return (String)get_Value("InvoiceWeekDay");
}
public static final int INVOICEWEEKDAYCUTOFF_AD_Reference_ID=167;
/** Sunday = 7 */
public static final String INVOICEWEEKDAYCUTOFF_Sunday = "7";
/** Monday = 1 */
public static final String INVOICEWEEKDAYCUTOFF_Monday = "1";
/** Tuesday = 2 */
public static final String INVOICEWEEKDAYCUTOFF_Tuesday = "2";
/** Wednesday = 3 */
public static final String INVOICEWEEKDAYCUTOFF_Wednesday = "3";
/** Thursday = 4 */
public static final String INVOICEWEEKDAYCUTOFF_Thursday = "4";
/** Friday = 5 */
public static final String INVOICEWEEKDAYCUTOFF_Friday = "5";
/** Saturday = 6 */
public static final String INVOICEWEEKDAYCUTOFF_Saturday = "6";
/** Set Invoice weekday cutoff.
Last day in the week for shipments to be included */
public void setInvoiceWeekDayCutoff (String InvoiceWeekDayCutoff)
{
if (InvoiceWeekDayCutoff == null || InvoiceWeekDayCutoff.equals("7") || InvoiceWeekDayCutoff.equals("1") || InvoiceWeekDayCutoff.equals("2") || InvoiceWeekDayCutoff.equals("3") || InvoiceWeekDayCutoff.equals("4") || InvoiceWeekDayCutoff.equals("5") || InvoiceWeekDayCutoff.equals("6"));
 else throw new IllegalArgumentException ("InvoiceWeekDayCutoff Invalid value - Reference_ID=167 - 7 - 1 - 2 - 3 - 4 - 5 - 6");
if (InvoiceWeekDayCutoff != null && InvoiceWeekDayCutoff.length() > 1)
{
log.warning("Length > 1 - truncated");
InvoiceWeekDayCutoff = InvoiceWeekDayCutoff.substring(0,0);
}
set_Value ("InvoiceWeekDayCutoff", InvoiceWeekDayCutoff);
}
/** Get Invoice weekday cutoff.
Last day in the week for shipments to be included */
public String getInvoiceWeekDayCutoff() 
{
return (String)get_Value("InvoiceWeekDayCutoff");
}
/** Set Amount Limit.
Send invoices only if the amount exceeds the limit */
public void setIsAmount (boolean IsAmount)
{
set_Value ("IsAmount", new Boolean(IsAmount));
}
/** Get Amount Limit.
Send invoices only if the amount exceeds the limit */
public boolean isAmount() 
{
Object oo = get_Value("IsAmount");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
}
