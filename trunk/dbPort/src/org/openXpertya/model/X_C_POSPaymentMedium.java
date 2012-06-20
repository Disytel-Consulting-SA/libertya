/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_POSPaymentMedium
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-12-13 16:49:22.809 */
public class X_C_POSPaymentMedium extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_POSPaymentMedium (Properties ctx, int C_POSPaymentMedium_ID, String trxName)
{
super (ctx, C_POSPaymentMedium_ID, trxName);
/** if (C_POSPaymentMedium_ID == 0)
{
setC_Currency_ID (0);	// @$C_Currency_ID@
setCheckDeadLine (null);
setContext (null);	// B
setC_POSPaymentMedium_ID (0);
setDateFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateTo (new Timestamp(System.currentTimeMillis()));
setM_EntidadFinanciera_ID (0);
setName (null);
setTenderType (null);	// 'CA'
}
 */
}
/** Load Constructor */
public X_C_POSPaymentMedium (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_POSPaymentMedium");

/** TableName=C_POSPaymentMedium */
public static final String Table_Name="C_POSPaymentMedium";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_POSPaymentMedium");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_POSPaymentMedium[").append(getID()).append("]");
return sb.toString();
}
public static final int BANK_AD_Reference_ID = MReference.getReferenceID("Bank List");
/** Others Banks = O */
public static final String BANK_OthersBanks = "O";
/** Nacion = BN */
public static final String BANK_Nacion = "BN";
/** Frances = BF */
public static final String BANK_Frances = "BF";
/** Galicia = BG */
public static final String BANK_Galicia = "BG";
/** Hipotecario = BH */
public static final String BANK_Hipotecario = "BH";
/** Santander = BS */
public static final String BANK_Santander = "BS";
/** HSBC = BH */
public static final String BANK_HSBC = "BH";
/** Set Bank.
Bank */
public void setBank (String Bank)
{
if (Bank == null || Bank.equals("O") || Bank.equals("BN") || Bank.equals("BF") || Bank.equals("BG") || Bank.equals("BH") || Bank.equals("BS") || Bank.equals("BH"));
 else throw new IllegalArgumentException ("Bank Invalid value - Reference = BANK_AD_Reference_ID - O - BN - BF - BG - BH - BS - BH");
if (Bank != null && Bank.length() > 2)
{
log.warning("Length > 2 - truncated");
Bank = Bank.substring(0,2);
}
set_Value ("Bank", Bank);
}
/** Get Bank.
Bank */
public String getBank() 
{
return (String)get_Value("Bank");
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CHECKDEADLINE_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium Check Dead Line");
/** 30 = 30 */
public static final String CHECKDEADLINE_30 = "30";
/** 60 = 60 */
public static final String CHECKDEADLINE_60 = "60";
/** 90 = 90 */
public static final String CHECKDEADLINE_90 = "90";
/** Today = 0 */
public static final String CHECKDEADLINE_Today = "0";
/** Set Check Dead Line.
Check Dead Line */
public void setCheckDeadLine (String CheckDeadLine)
{
if (CheckDeadLine.equals("30") || CheckDeadLine.equals("60") || CheckDeadLine.equals("90") || CheckDeadLine.equals("0"));
 else throw new IllegalArgumentException ("CheckDeadLine Invalid value - Reference = CHECKDEADLINE_AD_Reference_ID - 30 - 60 - 90 - 0");
if (CheckDeadLine == null) throw new IllegalArgumentException ("CheckDeadLine is mandatory");
if (CheckDeadLine.length() > 5)
{
log.warning("Length > 5 - truncated");
CheckDeadLine = CheckDeadLine.substring(0,5);
}
set_Value ("CheckDeadLine", CheckDeadLine);
}
/** Get Check Dead Line.
Check Dead Line */
public String getCheckDeadLine() 
{
return (String)get_Value("CheckDeadLine");
}
public static final int CONTEXT_AD_Reference_ID = MReference.getReferenceID("Payment medium context");
/** Both = B */
public static final String CONTEXT_Both = "B";
/** POS Only = P */
public static final String CONTEXT_POSOnly = "P";
/** Customer Receipts Only = R */
public static final String CONTEXT_CustomerReceiptsOnly = "R";
/** Set Payment Medium Context */
public void setContext (String Context)
{
if (Context.equals("B") || Context.equals("P") || Context.equals("R"));
 else throw new IllegalArgumentException ("Context Invalid value - Reference = CONTEXT_AD_Reference_ID - B - P - R");
if (Context == null) throw new IllegalArgumentException ("Context is mandatory");
if (Context.length() > 1)
{
log.warning("Length > 1 - truncated");
Context = Context.substring(0,1);
}
set_Value ("Context", Context);
}
/** Get Payment Medium Context */
public String getContext() 
{
return (String)get_Value("Context");
}
/** Set POS Payment Medium.
POS Terminal Payment Medium */
public void setC_POSPaymentMedium_ID (int C_POSPaymentMedium_ID)
{
set_ValueNoCheck ("C_POSPaymentMedium_ID", new Integer(C_POSPaymentMedium_ID));
}
/** Get POS Payment Medium.
POS Terminal Payment Medium */
public int getC_POSPaymentMedium_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date From.
Starting date for a range */
public void setDateFrom (Timestamp DateFrom)
{
if (DateFrom == null) throw new IllegalArgumentException ("DateFrom is mandatory");
set_Value ("DateFrom", DateFrom);
}
/** Get Date From.
Starting date for a range */
public Timestamp getDateFrom() 
{
return (Timestamp)get_Value("DateFrom");
}
/** Set Date To.
End date of a date range */
public void setDateTo (Timestamp DateTo)
{
if (DateTo == null) throw new IllegalArgumentException ("DateTo is mandatory");
set_Value ("DateTo", DateTo);
}
/** Get Date To.
End date of a date range */
public Timestamp getDateTo() 
{
return (Timestamp)get_Value("DateTo");
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
/** Set Discount Schema.
Schema to calculate the trade discount percentage */
public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
{
if (M_DiscountSchema_ID <= 0) set_Value ("M_DiscountSchema_ID", null);
 else 
set_Value ("M_DiscountSchema_ID", new Integer(M_DiscountSchema_ID));
}
/** Get Discount Schema.
Schema to calculate the trade discount percentage */
public int getM_DiscountSchema_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Entidad Financiera */
public void setM_EntidadFinanciera_ID (int M_EntidadFinanciera_ID)
{
set_Value ("M_EntidadFinanciera_ID", new Integer(M_EntidadFinanciera_ID));
}
/** Get Entidad Financiera */
public int getM_EntidadFinanciera_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinanciera_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int TENDERTYPE_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium Tender Type");
/** Direct Deposit = A */
public static final String TENDERTYPE_DirectDeposit = "A";
/** Credit Card = C */
public static final String TENDERTYPE_CreditCard = "C";
/** Cash = CA */
public static final String TENDERTYPE_Cash = "CA";
/** Check = K */
public static final String TENDERTYPE_Check = "K";
/** Credit Note = N */
public static final String TENDERTYPE_CreditNote = "N";
/** Credit = CR */
public static final String TENDERTYPE_Credit = "CR";
/** Retencion = RE */
public static final String TENDERTYPE_Retencion = "RE";
/** Advance Receipt = AC */
public static final String TENDERTYPE_AdvanceReceipt = "AC";
/** Set Tender type.
Method of Payment */
public void setTenderType (String TenderType)
{
if (TenderType.equals("A") || TenderType.equals("C") || TenderType.equals("CA") || TenderType.equals("K") || TenderType.equals("N") || TenderType.equals("CR") || TenderType.equals("RE") || TenderType.equals("AC"));
 else throw new IllegalArgumentException ("TenderType Invalid value - Reference = TENDERTYPE_AD_Reference_ID - A - C - CA - K - N - CR - RE - AC");
if (TenderType == null) throw new IllegalArgumentException ("TenderType is mandatory");
if (TenderType.length() > 2)
{
log.warning("Length > 2 - truncated");
TenderType = TenderType.substring(0,2);
}
set_Value ("TenderType", TenderType);
}
/** Get Tender type.
Method of Payment */
public String getTenderType() 
{
return (String)get_Value("TenderType");
}
}
