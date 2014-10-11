/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentTerm
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-10-11 02:57:25.222 */
public class X_C_PaymentTerm extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PaymentTerm (Properties ctx, int C_PaymentTerm_ID, String trxName)
{
super (ctx, C_PaymentTerm_ID, trxName);
/** if (C_PaymentTerm_ID == 0)
{
setAfterDelivery (false);
setApplicationDate (null);	// Y
setC_PaymentTerm_ID (0);
setDiscount (Env.ZERO);
setDiscount2 (Env.ZERO);
setDiscountDays (0);
setDiscountDays2 (0);
setGraceDays (0);
setGraceDays2 (0);
setIsDueFixed (false);
setIsValid (false);
setName (null);
setNetDays (0);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_PaymentTerm (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PaymentTerm");

/** TableName=C_PaymentTerm */
public static final String Table_Name="C_PaymentTerm";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PaymentTerm");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentTerm[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set After Delivery.
Due after delivery rather than after invoicing */
public void setAfterDelivery (boolean AfterDelivery)
{
set_Value ("AfterDelivery", new Boolean(AfterDelivery));
}
/** Get After Delivery.
Due after delivery rather than after invoicing */
public boolean isAfterDelivery() 
{
Object oo = get_Value("AfterDelivery");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int APPLICATIONDATE_AD_Reference_ID = MReference.getReferenceID("Payment term application date");
/** Invoice date = I */
public static final String APPLICATIONDATE_InvoiceDate = "I";
/** Reception date = R */
public static final String APPLICATIONDATE_ReceptionDate = "R";
/** Set Application date */
public void setApplicationDate (String ApplicationDate)
{
if (ApplicationDate.equals("I") || ApplicationDate.equals("R"));
 else throw new IllegalArgumentException ("ApplicationDate Invalid value - Reference = APPLICATIONDATE_AD_Reference_ID - I - R");
if (ApplicationDate == null) throw new IllegalArgumentException ("ApplicationDate is mandatory");
if (ApplicationDate.length() > 1)
{
log.warning("Length > 1 - truncated");
ApplicationDate = ApplicationDate.substring(0,1);
}
set_Value ("ApplicationDate", ApplicationDate);
}
/** Get Application date */
public String getApplicationDate() 
{
return (String)get_Value("ApplicationDate");
}
/** Set Payment Term.
The terms for Payment of this transaction */
public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
{
set_ValueNoCheck ("C_PaymentTerm_ID", new Integer(C_PaymentTerm_ID));
}
/** Get Payment Term.
The terms for Payment of this transaction */
public int getC_PaymentTerm_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Payment Medium.
POS Terminal Payment Medium */
public void setC_POSPaymentMedium_ID (int C_POSPaymentMedium_ID)
{
if (C_POSPaymentMedium_ID <= 0) set_Value ("C_POSPaymentMedium_ID", null);
 else 
set_Value ("C_POSPaymentMedium_ID", new Integer(C_POSPaymentMedium_ID));
}
/** Get POS Payment Medium.
POS Terminal Payment Medium */
public int getC_POSPaymentMedium_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_ID");
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
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Discount %.
Discount in percent */
public void setDiscount (BigDecimal Discount)
{
if (Discount == null) throw new IllegalArgumentException ("Discount is mandatory");
set_Value ("Discount", Discount);
}
/** Get Discount %.
Discount in percent */
public BigDecimal getDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("Discount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Discount 2 %.
Discount in percent */
public void setDiscount2 (BigDecimal Discount2)
{
if (Discount2 == null) throw new IllegalArgumentException ("Discount2 is mandatory");
set_Value ("Discount2", Discount2);
}
/** Get Discount 2 %.
Discount in percent */
public BigDecimal getDiscount2() 
{
BigDecimal bd = (BigDecimal)get_Value("Discount2");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int DISCOUNTAPPLICATIONTYPE_AD_Reference_ID = MReference.getReferenceID("Discount application type");
/** Daily increase = D */
public static final String DISCOUNTAPPLICATIONTYPE_DailyIncrease = "D";
/** No incremental = N */
public static final String DISCOUNTAPPLICATIONTYPE_NoIncremental = "N";
/** Set Discount Application Type */
public void setDiscountApplicationType (String DiscountApplicationType)
{
if (DiscountApplicationType == null || DiscountApplicationType.equals("D") || DiscountApplicationType.equals("N"));
 else throw new IllegalArgumentException ("DiscountApplicationType Invalid value - Reference = DISCOUNTAPPLICATIONTYPE_AD_Reference_ID - D - N");
if (DiscountApplicationType != null && DiscountApplicationType.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountApplicationType = DiscountApplicationType.substring(0,1);
}
set_Value ("DiscountApplicationType", DiscountApplicationType);
}
/** Get Discount Application Type */
public String getDiscountApplicationType() 
{
return (String)get_Value("DiscountApplicationType");
}
public static final int DISCOUNTAPPLICATIONTYPE2_AD_Reference_ID = MReference.getReferenceID("Discount application type");
/** Daily increase = D */
public static final String DISCOUNTAPPLICATIONTYPE2_DailyIncrease = "D";
/** No incremental = N */
public static final String DISCOUNTAPPLICATIONTYPE2_NoIncremental = "N";
/** Set Discount Application Type 2 */
public void setDiscountApplicationType2 (String DiscountApplicationType2)
{
if (DiscountApplicationType2 == null || DiscountApplicationType2.equals("D") || DiscountApplicationType2.equals("N"));
 else throw new IllegalArgumentException ("DiscountApplicationType2 Invalid value - Reference = DISCOUNTAPPLICATIONTYPE2_AD_Reference_ID - D - N");
if (DiscountApplicationType2 != null && DiscountApplicationType2.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountApplicationType2 = DiscountApplicationType2.substring(0,1);
}
set_Value ("DiscountApplicationType2", DiscountApplicationType2);
}
/** Get Discount Application Type 2 */
public String getDiscountApplicationType2() 
{
return (String)get_Value("DiscountApplicationType2");
}
/** Set Discount Days.
Number of days from invoice date to be eligible for discount */
public void setDiscountDays (int DiscountDays)
{
set_Value ("DiscountDays", new Integer(DiscountDays));
}
/** Get Discount Days.
Number of days from invoice date to be eligible for discount */
public int getDiscountDays() 
{
Integer ii = (Integer)get_Value("DiscountDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Discount Days 2.
Number of days from invoice date to be eligible for discount */
public void setDiscountDays2 (int DiscountDays2)
{
set_Value ("DiscountDays2", new Integer(DiscountDays2));
}
/** Get Discount Days 2.
Number of days from invoice date to be eligible for discount */
public int getDiscountDays2() 
{
Integer ii = (Integer)get_Value("DiscountDays2");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Note.
Additional information for a Document */
public void setDocumentNote (String DocumentNote)
{
if (DocumentNote != null && DocumentNote.length() > 2000)
{
log.warning("Length > 2000 - truncated");
DocumentNote = DocumentNote.substring(0,2000);
}
set_Value ("DocumentNote", DocumentNote);
}
/** Get Document Note.
Additional information for a Document */
public String getDocumentNote() 
{
return (String)get_Value("DocumentNote");
}
/** Set Fix month cutoff.
Last day to include for next due date */
public void setFixMonthCutoff (int FixMonthCutoff)
{
set_Value ("FixMonthCutoff", new Integer(FixMonthCutoff));
}
/** Get Fix month cutoff.
Last day to include for next due date */
public int getFixMonthCutoff() 
{
Integer ii = (Integer)get_Value("FixMonthCutoff");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fix month day.
Day of the month of the due date */
public void setFixMonthDay (int FixMonthDay)
{
set_Value ("FixMonthDay", new Integer(FixMonthDay));
}
/** Get Fix month day.
Day of the month of the due date */
public int getFixMonthDay() 
{
Integer ii = (Integer)get_Value("FixMonthDay");
if (ii == null) return 0;
return ii.intValue();
}
/** Set FixMonthDay2 */
public void setFixMonthDay2 (int FixMonthDay2)
{
set_Value ("FixMonthDay2", new Integer(FixMonthDay2));
}
/** Get FixMonthDay2 */
public int getFixMonthDay2() 
{
Integer ii = (Integer)get_Value("FixMonthDay2");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Fix month offset.
Number of months (0=same, 1=following) */
public void setFixMonthOffset (int FixMonthOffset)
{
set_Value ("FixMonthOffset", new Integer(FixMonthOffset));
}
/** Get Fix month offset.
Number of months (0=same, 1=following) */
public int getFixMonthOffset() 
{
Integer ii = (Integer)get_Value("FixMonthOffset");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Grace Days.
Days after due date to send first dunning letter */
public void setGraceDays (int GraceDays)
{
set_Value ("GraceDays", new Integer(GraceDays));
}
/** Get Grace Days.
Days after due date to send first dunning letter */
public int getGraceDays() 
{
Integer ii = (Integer)get_Value("GraceDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Grace Days 2 */
public void setGraceDays2 (int GraceDays2)
{
set_Value ("GraceDays2", new Integer(GraceDays2));
}
/** Get Grace Days 2 */
public int getGraceDays2() 
{
Integer ii = (Integer)get_Value("GraceDays2");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Fixed due date.
Payment is due on a fixed date */
public void setIsDueFixed (boolean IsDueFixed)
{
set_Value ("IsDueFixed", new Boolean(IsDueFixed));
}
/** Get Fixed due date.
Payment is due on a fixed date */
public boolean isDueFixed() 
{
Object oo = get_Value("IsDueFixed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Next Business Day.
Payment due on the next business day */
public void setIsNextBusinessDay (boolean IsNextBusinessDay)
{
set_Value ("IsNextBusinessDay", new Boolean(IsNextBusinessDay));
}
/** Get Next Business Day.
Payment due on the next business day */
public boolean isNextBusinessDay() 
{
Object oo = get_Value("IsNextBusinessDay");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Valid.
Element is valid */
public void setIsValid (boolean IsValid)
{
set_Value ("IsValid", new Boolean(IsValid));
}
/** Get Valid.
Element is valid */
public boolean isValid() 
{
Object oo = get_Value("IsValid");
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
Name = Name.substring(0,60);
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
public static final int NETDAY_AD_Reference_ID = MReference.getReferenceID("Weekdays");
/** Sunday = 7 */
public static final String NETDAY_Sunday = "7";
/** Monday = 1 */
public static final String NETDAY_Monday = "1";
/** Tuesday = 2 */
public static final String NETDAY_Tuesday = "2";
/** Wednesday = 3 */
public static final String NETDAY_Wednesday = "3";
/** Thursday = 4 */
public static final String NETDAY_Thursday = "4";
/** Friday = 5 */
public static final String NETDAY_Friday = "5";
/** Saturday = 6 */
public static final String NETDAY_Saturday = "6";
/** Set Net Day.
Day when payment is due net */
public void setNetDay (String NetDay)
{
if (NetDay == null || NetDay.equals("7") || NetDay.equals("1") || NetDay.equals("2") || NetDay.equals("3") || NetDay.equals("4") || NetDay.equals("5") || NetDay.equals("6"));
 else throw new IllegalArgumentException ("NetDay Invalid value - Reference = NETDAY_AD_Reference_ID - 7 - 1 - 2 - 3 - 4 - 5 - 6");
if (NetDay != null && NetDay.length() > 1)
{
log.warning("Length > 1 - truncated");
NetDay = NetDay.substring(0,1);
}
set_Value ("NetDay", NetDay);
}
/** Get Net Day.
Day when payment is due net */
public String getNetDay() 
{
return (String)get_Value("NetDay");
}
/** Set Net Days.
Net Days in which payment is due */
public void setNetDays (int NetDays)
{
set_Value ("NetDays", new Integer(NetDays));
}
/** Get Net Days.
Net Days in which payment is due */
public int getNetDays() 
{
Integer ii = (Integer)get_Value("NetDays");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
