/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CheckPrintingLines
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-18 16:18:10.499 */
public class X_C_CheckPrintingLines extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CheckPrintingLines (Properties ctx, int C_CheckPrintingLines_ID, String trxName)
{
super (ctx, C_CheckPrintingLines_ID, trxName);
/** if (C_CheckPrintingLines_ID == 0)
{
setC_BPartner_ID (0);
setC_Checkprinting_ID (0);
setC_Checkprintinglines_ID (0);
setC_Currency_ID (0);
setC_Payment_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));
setDocStatus (null);
setPayAmt (Env.ZERO);
setPrint (false);
setPrinted (false);
setTenderType (null);
}
 */
}
/** Load Constructor */
public X_C_CheckPrintingLines (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CheckPrintingLines");

/** TableName=C_CheckPrintingLines */
public static final String Table_Name="C_CheckPrintingLines";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CheckPrintingLines");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CheckPrintingLines[").append(getID()).append("]");
return sb.toString();
}
public static final int C_BPARTNER_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (all)");
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CHECKPRINTING_ID_AD_Reference_ID = MReference.getReferenceID("Check Printing");
/** Set C_Checkprinting_ID */
public void setC_Checkprinting_ID (int C_Checkprinting_ID)
{
set_Value ("C_Checkprinting_ID", new Integer(C_Checkprinting_ID));
}
/** Get C_Checkprinting_ID */
public int getC_Checkprinting_ID() 
{
Integer ii = (Integer)get_Value("C_Checkprinting_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Checkprintinglines_ID */
public void setC_Checkprintinglines_ID (int C_Checkprintinglines_ID)
{
set_ValueNoCheck ("C_Checkprintinglines_ID", new Integer(C_Checkprintinglines_ID));
}
/** Get C_Checkprintinglines_ID */
public int getC_Checkprintinglines_ID() 
{
Integer ii = (Integer)get_Value("C_Checkprintinglines_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CURRENCY_ID_AD_Reference_ID = MReference.getReferenceID("C_Currency");
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
/** Set Check No.
Check Number */
public void setCheckNo (String CheckNo)
{
if (CheckNo != null && CheckNo.length() > 20)
{
log.warning("Length > 20 - truncated");
CheckNo = CheckNo.substring(0,20);
}
set_Value ("CheckNo", CheckNo);
}
/** Get Check No.
Check Number */
public String getCheckNo() 
{
return (String)get_Value("CheckNo");
}
public static final int C_PAYMENT_ID_AD_Reference_ID = MReference.getReferenceID("C_Payment");
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date Emission Check */
public void setDateEmissionCheck (Timestamp DateEmissionCheck)
{
set_Value ("DateEmissionCheck", DateEmissionCheck);
}
/** Get Date Emission Check */
public Timestamp getDateEmissionCheck() 
{
return (Timestamp)get_Value("DateEmissionCheck");
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
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
public static final int DOCSTATUS_AD_Reference_ID = MReference.getReferenceID("_Document Status");
/** Voided = VO */
public static final String DOCSTATUS_Voided = "VO";
/** Not Approved = NA */
public static final String DOCSTATUS_NotApproved = "NA";
/** In Progress = IP */
public static final String DOCSTATUS_InProgress = "IP";
/** Completed = CO */
public static final String DOCSTATUS_Completed = "CO";
/** Approved = AP */
public static final String DOCSTATUS_Approved = "AP";
/** Closed = CL */
public static final String DOCSTATUS_Closed = "CL";
/** Waiting Confirmation = WC */
public static final String DOCSTATUS_WaitingConfirmation = "WC";
/** Waiting Payment = WP */
public static final String DOCSTATUS_WaitingPayment = "WP";
/** Unknown = ?? */
public static final String DOCSTATUS_Unknown = "??";
/** Drafted = DR */
public static final String DOCSTATUS_Drafted = "DR";
/** Invalid = IN */
public static final String DOCSTATUS_Invalid = "IN";
/** Reversed = RE */
public static final String DOCSTATUS_Reversed = "RE";
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference = DOCSTATUS_AD_Reference_ID - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN - RE");
if (DocStatus == null) throw new IllegalArgumentException ("DocStatus is mandatory");
if (DocStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
DocStatus = DocStatus.substring(0,2);
}
set_Value ("DocStatus", DocStatus);
}
/** Get Document Status.
The current status of the document */
public String getDocStatus() 
{
return (String)get_Value("DocStatus");
}
/** Set Payment amount.
Amount being paid */
public void setPayAmt (BigDecimal PayAmt)
{
if (PayAmt == null) throw new IllegalArgumentException ("PayAmt is mandatory");
set_Value ("PayAmt", PayAmt);
}
/** Get Payment amount.
Amount being paid */
public BigDecimal getPayAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PayAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Print */
public void setPrint (boolean Print)
{
set_Value ("Print", new Boolean(Print));
}
/** Get Print */
public boolean isPrint() 
{
Object oo = get_Value("Print");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Printed */
public void setPrinted (boolean Printed)
{
set_Value ("Printed", new Boolean(Printed));
}
/** Get Printed */
public boolean isPrinted() 
{
Object oo = get_Value("Printed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
if (TenderType.length() > 1)
{
log.warning("Length > 1 - truncated");
TenderType = TenderType.substring(0,1);
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
