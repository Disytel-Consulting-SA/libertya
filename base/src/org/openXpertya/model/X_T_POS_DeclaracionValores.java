/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_POS_DeclaracionValores
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-02-06 14:09:35.905 */
public class X_T_POS_DeclaracionValores extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_POS_DeclaracionValores (Properties ctx, int T_POS_DeclaracionValores_ID, String trxName)
{
super (ctx, T_POS_DeclaracionValores_ID, trxName);
/** if (T_POS_DeclaracionValores_ID == 0)
{
setAD_PInstance_ID (0);
setT_POS_DeclaracionValores_ID (0);
}
 */
}
/** Load Constructor */
public X_T_POS_DeclaracionValores (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_POS_DeclaracionValores");

/** TableName=T_POS_DeclaracionValores */
public static final String Table_Name="T_POS_DeclaracionValores";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_POS_DeclaracionValores");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_POS_DeclaracionValores[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
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
/** Set Allocation Active */
public void setAllocation_Active (boolean Allocation_Active)
{
set_Value ("Allocation_Active", new Boolean(Allocation_Active));
}
/** Get Allocation Active */
public boolean isAllocation_Active() 
{
Object oo = get_Value("Allocation_Active");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Business Partner Entidad Financiera Name */
public void setBP_EntidadFinanciera_Name (String BP_EntidadFinanciera_Name)
{
if (BP_EntidadFinanciera_Name != null && BP_EntidadFinanciera_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
BP_EntidadFinanciera_Name = BP_EntidadFinanciera_Name.substring(0,60);
}
set_Value ("BP_EntidadFinanciera_Name", BP_EntidadFinanciera_Name);
}
/** Get Business Partner Entidad Financiera Name */
public String getBP_EntidadFinanciera_Name() 
{
return (String)get_Value("BP_EntidadFinanciera_Name");
}
/** Set Business Partner Entidad Financiera Value */
public void setBP_EntidadFinanciera_Value (String BP_EntidadFinanciera_Value)
{
if (BP_EntidadFinanciera_Value != null && BP_EntidadFinanciera_Value.length() > 40)
{
log.warning("Length > 40 - truncated");
BP_EntidadFinanciera_Value = BP_EntidadFinanciera_Value.substring(0,40);
}
set_Value ("BP_EntidadFinanciera_Value", BP_EntidadFinanciera_Value);
}
/** Get Business Partner Entidad Financiera Value */
public String getBP_EntidadFinanciera_Value() 
{
return (String)get_Value("BP_EntidadFinanciera_Value");
}
/** Set Category */
public void setCategory (String Category)
{
if (Category != null && Category.length() > 20)
{
log.warning("Length > 20 - truncated");
Category = Category.substring(0,20);
}
set_Value ("Category", Category);
}
/** Get Category */
public String getCategory() 
{
return (String)get_Value("Category");
}
/** Set Charge.
Additional document charges */
public void setC_Charge_ID (int C_Charge_ID)
{
if (C_Charge_ID <= 0) set_Value ("C_Charge_ID", null);
 else 
set_Value ("C_Charge_ID", new Integer(C_Charge_ID));
}
/** Get Charge.
Additional document charges */
public int getC_Charge_ID() 
{
Integer ii = (Integer)get_Value("C_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
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
/** Set Charge Name.
Name of the Charge */
public void setChargeName (String ChargeName)
{
if (ChargeName != null && ChargeName.length() > 60)
{
log.warning("Length > 60 - truncated");
ChargeName = ChargeName.substring(0,60);
}
set_Value ("ChargeName", ChargeName);
}
/** Get Charge Name.
Name of the Charge */
public String getChargeName() 
{
return (String)get_Value("ChargeName");
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Terminal.
Point of Sales Terminal */
public void setC_POS_ID (int C_POS_ID)
{
if (C_POS_ID <= 0) set_Value ("C_POS_ID", null);
 else 
set_Value ("C_POS_ID", new Integer(C_POS_ID));
}
/** Get POS Terminal.
Point of Sales Terminal */
public int getC_POS_ID() 
{
Integer ii = (Integer)get_Value("C_POS_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Journal.
POS Journal */
public void setC_POSJournal_ID (int C_POSJournal_ID)
{
if (C_POSJournal_ID <= 0) set_Value ("C_POSJournal_ID", null);
 else 
set_Value ("C_POSJournal_ID", new Integer(C_POSJournal_ID));
}
/** Get POS Journal.
POS Journal */
public int getC_POSJournal_ID() 
{
Integer ii = (Integer)get_Value("C_POSJournal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Card */
public void setCreditCard (String CreditCard)
{
if (CreditCard != null && CreditCard.length() > 100)
{
log.warning("Length > 100 - truncated");
CreditCard = CreditCard.substring(0,100);
}
set_Value ("CreditCard", CreditCard);
}
/** Get Credit Card */
public String getCreditCard() 
{
return (String)get_Value("CreditCard");
}
/** Set Cupon */
public void setCupon (String Cupon)
{
if (Cupon != null && Cupon.length() > 100)
{
log.warning("Length > 100 - truncated");
Cupon = Cupon.substring(0,100);
}
set_Value ("Cupon", Cupon);
}
/** Get Cupon */
public String getCupon() 
{
return (String)get_Value("Cupon");
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
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
/** Set doc_id */
public void setdoc_id (int doc_id)
{
set_Value ("doc_id", new Integer(doc_id));
}
/** Get doc_id */
public int getdoc_id() 
{
Integer ii = (Integer)get_Value("doc_id");
if (ii == null) return 0;
return ii.intValue();
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
if (DocStatus == null || DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE") || ( refContainsValue("CORE-AD_Reference-131", DocStatus) ) );
 else throw new IllegalArgumentException ("DocStatus Invalid value: " + DocStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-131") );
if (DocStatus != null && DocStatus.length() > 2)
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
/** Set Egreso */
public void setEgreso (BigDecimal Egreso)
{
set_Value ("Egreso", Egreso);
}
/** Get Egreso */
public BigDecimal getEgreso() 
{
BigDecimal bd = (BigDecimal)get_Value("Egreso");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Entidad Financiera Name */
public void setEntidadFinanciera_Name (String EntidadFinanciera_Name)
{
if (EntidadFinanciera_Name != null && EntidadFinanciera_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
EntidadFinanciera_Name = EntidadFinanciera_Name.substring(0,60);
}
set_Value ("EntidadFinanciera_Name", EntidadFinanciera_Name);
}
/** Get Entidad Financiera Name */
public String getEntidadFinanciera_Name() 
{
return (String)get_Value("EntidadFinanciera_Name");
}
/** Set Entidad Financiera Value */
public void setEntidadFinanciera_Value (String EntidadFinanciera_Value)
{
if (EntidadFinanciera_Value != null && EntidadFinanciera_Value.length() > 40)
{
log.warning("Length > 40 - truncated");
EntidadFinanciera_Value = EntidadFinanciera_Value.substring(0,40);
}
set_Value ("EntidadFinanciera_Value", EntidadFinanciera_Value);
}
/** Get Entidad Financiera Value */
public String getEntidadFinanciera_Value() 
{
return (String)get_Value("EntidadFinanciera_Value");
}
/** Set Generated Invoice Document No */
public void setGenerated_Invoice_DocumentNo (String Generated_Invoice_DocumentNo)
{
if (Generated_Invoice_DocumentNo != null && Generated_Invoice_DocumentNo.length() > 40)
{
log.warning("Length > 40 - truncated");
Generated_Invoice_DocumentNo = Generated_Invoice_DocumentNo.substring(0,40);
}
set_Value ("Generated_Invoice_DocumentNo", Generated_Invoice_DocumentNo);
}
/** Get Generated Invoice Document No */
public String getGenerated_Invoice_DocumentNo() 
{
return (String)get_Value("Generated_Invoice_DocumentNo");
}
/** Set Ingreso */
public void setIngreso (BigDecimal Ingreso)
{
set_Value ("Ingreso", Ingreso);
}
/** Get Ingreso */
public BigDecimal getIngreso() 
{
BigDecimal bd = (BigDecimal)get_Value("Ingreso");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoice DocumentNo */
public void setInvoice_DocumentNo (String Invoice_DocumentNo)
{
if (Invoice_DocumentNo != null && Invoice_DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
Invoice_DocumentNo = Invoice_DocumentNo.substring(0,30);
}
set_Value ("Invoice_DocumentNo", Invoice_DocumentNo);
}
/** Get Invoice DocumentNo */
public String getInvoice_DocumentNo() 
{
return (String)get_Value("Invoice_DocumentNo");
}
/** Set Invoice GrandTotal */
public void setInvoice_GrandTotal (BigDecimal Invoice_GrandTotal)
{
set_Value ("Invoice_GrandTotal", Invoice_GrandTotal);
}
/** Get Invoice GrandTotal */
public BigDecimal getInvoice_GrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("Invoice_GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set POS Name */
public void setPOSName (String POSName)
{
if (POSName != null && POSName.length() > 60)
{
log.warning("Length > 60 - truncated");
POSName = POSName.substring(0,60);
}
set_Value ("POSName", POSName);
}
/** Get POS Name */
public String getPOSName() 
{
return (String)get_Value("POSName");
}
public static final int TENDERTYPE_AD_Reference_ID = MReference.getReferenceID("C_POSJournalPayments_V Tender Type");
/** Direct Deposit = A */
public static final String TENDERTYPE_DirectDeposit = "A";
/** Check = K */
public static final String TENDERTYPE_Check = "K";
/** Cash = CA */
public static final String TENDERTYPE_Cash = "CA";
/** Credit Card = C */
public static final String TENDERTYPE_CreditCard = "C";
/** Direct Debit = D */
public static final String TENDERTYPE_DirectDebit = "D";
/** Credit = CR */
public static final String TENDERTYPE_Credit = "CR";
/** Set Tender type.
Method of Payment */
public void setTenderType (String TenderType)
{
if (TenderType == null || TenderType.equals("A") || TenderType.equals("K") || TenderType.equals("CA") || TenderType.equals("C") || TenderType.equals("D") || TenderType.equals("CR") || ( refContainsValue("CORE-AD_Reference-1010166", TenderType) ) );
 else throw new IllegalArgumentException ("TenderType Invalid value: " + TenderType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010166") );
if (TenderType != null && TenderType.length() > 3)
{
log.warning("Length > 3 - truncated");
TenderType = TenderType.substring(0,3);
}
set_Value ("TenderType", TenderType);
}
/** Get Tender type.
Method of Payment */
public String getTenderType() 
{
return (String)get_Value("TenderType");
}
/** Set T_POS_DeclaracionValores_ID */
public void setT_POS_DeclaracionValores_ID (int T_POS_DeclaracionValores_ID)
{
set_ValueNoCheck ("T_POS_DeclaracionValores_ID", new Integer(T_POS_DeclaracionValores_ID));
}
/** Get T_POS_DeclaracionValores_ID */
public int getT_POS_DeclaracionValores_ID() 
{
Integer ii = (Integer)get_Value("T_POS_DeclaracionValores_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
