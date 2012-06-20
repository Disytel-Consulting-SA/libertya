/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CentralAux
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-12-02 13:02:52.639 */
public class X_C_CentralAux extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CentralAux (Properties ctx, int C_CentralAux_ID, String trxName)
{
super (ctx, C_CentralAux_ID, trxName);
/** if (C_CentralAux_ID == 0)
{
setAmt (Env.ZERO);
setAuthCode (null);
setC_BPartner_ID (0);
setC_CentralAux_ID (0);
setC_DocType_ID (0);
setConfirmed (true);	// Y
setDateTrx (new Timestamp(System.currentTimeMillis()));	// @Date@
setDocStatus (null);
setDocType (null);
setPaymentRule (null);
setPrepayment (false);
setReconciled (false);
setRegisterType (null);	// OF
setTenderType (null);
setTransactionType (null);	// C
}
 */
}
/** Load Constructor */
public X_C_CentralAux (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CentralAux");

/** TableName=C_CentralAux */
public static final String Table_Name="C_CentralAux";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CentralAux");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CentralAux[").append(getID()).append("]");
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
/** Set Authorization Code */
public void setAuthCode (String AuthCode)
{
if (AuthCode == null) throw new IllegalArgumentException ("AuthCode is mandatory");
if (AuthCode.length() > 255)
{
log.warning("Length > 255 - truncated");
AuthCode = AuthCode.substring(0,255);
}
set_Value ("AuthCode", AuthCode);
}
/** Get Authorization Code */
public String getAuthCode() 
{
return (String)get_Value("AuthCode");
}
/** Set BPartnerUID */
public void setBPartnerUID (String BPartnerUID)
{
if (BPartnerUID != null && BPartnerUID.length() > 255)
{
log.warning("Length > 255 - truncated");
BPartnerUID = BPartnerUID.substring(0,255);
}
set_Value ("BPartnerUID", BPartnerUID);
}
/** Get BPartnerUID */
public String getBPartnerUID() 
{
return (String)get_Value("BPartnerUID");
}
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
/** Set C_CentralAux_ID */
public void setC_CentralAux_ID (int C_CentralAux_ID)
{
set_ValueNoCheck ("C_CentralAux_ID", new Integer(C_CentralAux_ID));
}
/** Get C_CentralAux_ID */
public int getC_CentralAux_ID() 
{
Integer ii = (Integer)get_Value("C_CentralAux_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Confirmed */
public void setConfirmed (boolean Confirmed)
{
set_Value ("Confirmed", new Boolean(Confirmed));
}
/** Get Confirmed */
public boolean isConfirmed() 
{
Object oo = get_Value("Confirmed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int DOCTYPE_AD_Reference_ID = MReference.getReferenceID("Auxiliar Doc Type");
/** Invoice = I */
public static final String DOCTYPE_Invoice = "I";
/** Payment/Receipt = P */
public static final String DOCTYPE_PaymentReceipt = "P";
/** Set Document Type */
public void setDocType (String DocType)
{
if (DocType.equals("I") || DocType.equals("P"));
 else throw new IllegalArgumentException ("DocType Invalid value - Reference = DOCTYPE_AD_Reference_ID - I - P");
if (DocType == null) throw new IllegalArgumentException ("DocType is mandatory");
if (DocType.length() > 1)
{
log.warning("Length > 1 - truncated");
DocType = DocType.substring(0,1);
}
set_Value ("DocType", DocType);
}
/** Get Document Type */
public String getDocType() 
{
return (String)get_Value("DocType");
}
/** Set Document Type Key.
Clave única de identificación del tipo de documento */
public void setDocTypeKey (String DocTypeKey)
{
if (DocTypeKey != null && DocTypeKey.length() > 40)
{
log.warning("Length > 40 - truncated");
DocTypeKey = DocTypeKey.substring(0,40);
}
set_Value ("DocTypeKey", DocTypeKey);
}
/** Get Document Type Key.
Clave única de identificación del tipo de documento */
public String getDocTypeKey() 
{
return (String)get_Value("DocTypeKey");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Document Record ID */
public void setDocumentRecord_ID (int DocumentRecord_ID)
{
if (DocumentRecord_ID <= 0) set_Value ("DocumentRecord_ID", null);
 else 
set_Value ("DocumentRecord_ID", new Integer(DocumentRecord_ID));
}
/** Get Document Record ID */
public int getDocumentRecord_ID() 
{
Integer ii = (Integer)get_Value("DocumentRecord_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Unique ID */
public void setDocumentUID (String DocumentUID)
{
if (DocumentUID != null && DocumentUID.length() > 255)
{
log.warning("Length > 255 - truncated");
DocumentUID = DocumentUID.substring(0,255);
}
set_Value ("DocumentUID", DocumentUID);
}
/** Get Document Unique ID */
public String getDocumentUID() 
{
return (String)get_Value("DocumentUID");
}
/** Set Due Date.
Due Date */
public void setDueDate (Timestamp DueDate)
{
set_Value ("DueDate", DueDate);
}
/** Get Due Date.
Due Date */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
}
public static final int PAYMENTRULE_AD_Reference_ID = MReference.getReferenceID("All_Payment Rule");
/** Transfer = Tr */
public static final String PAYMENTRULE_Transfer = "Tr";
/** Credit Card = K */
public static final String PAYMENTRULE_CreditCard = "K";
/** Cash = B */
public static final String PAYMENTRULE_Cash = "B";
/** On Credit = P */
public static final String PAYMENTRULE_OnCredit = "P";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("S") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D"));
 else throw new IllegalArgumentException ("PaymentRule Invalid value - Reference = PAYMENTRULE_AD_Reference_ID - Tr - K - B - P - S - PC - T - Cf - D");
if (PaymentRule == null) throw new IllegalArgumentException ("PaymentRule is mandatory");
if (PaymentRule.length() > 2)
{
log.warning("Length > 2 - truncated");
PaymentRule = PaymentRule.substring(0,2);
}
set_Value ("PaymentRule", PaymentRule);
}
/** Get Payment Rule.
How you pay the invoice */
public String getPaymentRule() 
{
return (String)get_Value("PaymentRule");
}
/** Set Prepayment */
public void setPrepayment (boolean Prepayment)
{
set_Value ("Prepayment", new Boolean(Prepayment));
}
/** Get Prepayment */
public boolean isPrepayment() 
{
Object oo = get_Value("Prepayment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reconciled */
public void setReconciled (boolean Reconciled)
{
set_Value ("Reconciled", new Boolean(Reconciled));
}
/** Get Reconciled */
public boolean isReconciled() 
{
Object oo = get_Value("Reconciled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int REGISTERTYPE_AD_Reference_ID = MReference.getReferenceID("Auxiliar Register Type");
/** Online = ON */
public static final String REGISTERTYPE_Online = "ON";
/** Offline = OF */
public static final String REGISTERTYPE_Offline = "OF";
/** Set Register Type */
public void setRegisterType (String RegisterType)
{
if (RegisterType.equals("ON") || RegisterType.equals("OF"));
 else throw new IllegalArgumentException ("RegisterType Invalid value - Reference = REGISTERTYPE_AD_Reference_ID - ON - OF");
if (RegisterType == null) throw new IllegalArgumentException ("RegisterType is mandatory");
if (RegisterType.length() > 2)
{
log.warning("Length > 2 - truncated");
RegisterType = RegisterType.substring(0,2);
}
set_Value ("RegisterType", RegisterType);
}
/** Get Register Type */
public String getRegisterType() 
{
return (String)get_Value("RegisterType");
}
/** Set Sign */
public void setSign (int Sign)
{
set_Value ("Sign", new Integer(Sign));
}
/** Get Sign */
public int getSign() 
{
Integer ii = (Integer)get_Value("Sign");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Tender type.
Method of Payment */
public void setTenderType (String TenderType)
{
if (TenderType.equals("A") || TenderType.equals("C") || TenderType.equals("CA") || TenderType.equals("K") || TenderType.equals("N") || TenderType.equals("CR"));
 else throw new IllegalArgumentException ("TenderType Invalid value - Reference = TENDERTYPE_AD_Reference_ID - A - C - CA - K - N - CR");
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
public static final int TRANSACTIONTYPE_AD_Reference_ID = MReference.getReferenceID("Transaction Type");
/** Both = B */
public static final String TRANSACTIONTYPE_Both = "B";
/** Customer = C */
public static final String TRANSACTIONTYPE_Customer = "C";
/** Vendor = V */
public static final String TRANSACTIONTYPE_Vendor = "V";
/** Set Transaction Type */
public void setTransactionType (String TransactionType)
{
if (TransactionType.equals("B") || TransactionType.equals("C") || TransactionType.equals("V"));
 else throw new IllegalArgumentException ("TransactionType Invalid value - Reference = TRANSACTIONTYPE_AD_Reference_ID - B - C - V");
if (TransactionType == null) throw new IllegalArgumentException ("TransactionType is mandatory");
if (TransactionType.length() > 1)
{
log.warning("Length > 1 - truncated");
TransactionType = TransactionType.substring(0,1);
}
set_Value ("TransactionType", TransactionType);
}
/** Get Transaction Type */
public String getTransactionType() 
{
return (String)get_Value("TransactionType");
}
}
