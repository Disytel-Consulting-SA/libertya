/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaySchedule
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-01-19 17:17:30.231 */
public class X_C_PaySchedule extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PaySchedule (Properties ctx, int C_PaySchedule_ID, String trxName)
{
super (ctx, C_PaySchedule_ID, trxName);
/** if (C_PaySchedule_ID == 0)
{
setC_PaymentTerm_ID (0);
setC_PaySchedule_ID (0);
setDiscount (Env.ZERO);
setDiscountDays (0);
setGraceDays (0);
setIsValid (false);
setNetDays (0);
setPercentage (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_PaySchedule (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PaySchedule");

/** TableName=C_PaySchedule */
public static final String Table_Name="C_PaySchedule";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PaySchedule");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaySchedule[").append(getID()).append("]");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_PaymentTerm_ID()));
}
/** Set Payment Schedule.
Payment Schedule Template */
public void setC_PaySchedule_ID (int C_PaySchedule_ID)
{
set_ValueNoCheck ("C_PaySchedule_ID", new Integer(C_PaySchedule_ID));
}
/** Get Payment Schedule.
Payment Schedule Template */
public int getC_PaySchedule_ID() 
{
Integer ii = (Integer)get_Value("C_PaySchedule_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Percentage.
Percent of the entire amount */
public void setPercentage (BigDecimal Percentage)
{
if (Percentage == null) throw new IllegalArgumentException ("Percentage is mandatory");
set_Value ("Percentage", Percentage);
}
/** Get Percentage.
Percent of the entire amount */
public BigDecimal getPercentage() 
{
BigDecimal bd = (BigDecimal)get_Value("Percentage");
if (bd == null) return Env.ZERO;
return bd;
}
}
