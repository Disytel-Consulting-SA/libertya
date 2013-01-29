/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_BoletaDeposito
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-07-08 09:14:26.194 */
public class X_M_BoletaDeposito extends PO
{
/** Constructor estándar */
public X_M_BoletaDeposito (Properties ctx, int M_BoletaDeposito_ID, String trxName)
{
super (ctx, M_BoletaDeposito_ID, trxName);
/** if (M_BoletaDeposito_ID == 0)
{
setBoletaAction (null);	// C
setC_BankAccount_ID (0);
setC_BPartner_ID (0);
setC_Currency_ID (0);
setC_DocType_ID (0);
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setFechaDeposito (new Timestamp(System.currentTimeMillis()));	// @#Date@
setGrandTotal (Env.ZERO);
setIsReconciled (false);	// N
setM_BoletaDeposito_ID (0);
setPosted (false);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_M_BoletaDeposito (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000129 */
public static final int Table_ID=1000129;

/** TableName=M_BoletaDeposito */
public static final String Table_Name="M_BoletaDeposito";

protected static KeyNamePair Model = new KeyNamePair(1000129,"M_BoletaDeposito");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_BoletaDeposito[").append(getID()).append("]");
return sb.toString();
}
public static final int BOLETAACTION_AD_Reference_ID=1010094;
/** Document For Each Check = C */
public static final String BOLETAACTION_DocumentForEachCheck = "C";
/** Document For Boleta = A */
public static final String BOLETAACTION_DocumentForBoleta = "A";
/** Set Boleta Action.
Boleta Action */
public void setBoletaAction (String BoletaAction)
{
if (BoletaAction.equals("C") || BoletaAction.equals("A"));
 else throw new IllegalArgumentException ("BoletaAction Invalid value - Reference_ID=1010094 - C - A");
if (BoletaAction == null) throw new IllegalArgumentException ("BoletaAction is mandatory");
if (BoletaAction.length() > 2)
{
log.warning("Length > 2 - truncated");
BoletaAction = BoletaAction.substring(0,2);
}
set_Value ("BoletaAction", BoletaAction);
}
/** Get Boleta Action.
Boleta Action */
public String getBoletaAction() 
{
return (String)get_Value("BoletaAction");
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
set_Value ("C_BankAccount_ID", new Integer(C_BankAccount_ID));
}
/** Get Bank Account.
Account at the Bank */
public int getC_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BOLETA_PAYMENT_ID_AD_Reference_ID=343;
/** Set Payment Of Boleta.
Payment Of Boleta */
public void setC_Boleta_Payment_ID (int C_Boleta_Payment_ID)
{
if (C_Boleta_Payment_ID <= 0) set_Value ("C_Boleta_Payment_ID", null);
 else 
set_Value ("C_Boleta_Payment_ID", new Integer(C_Boleta_Payment_ID));
}
/** Get Payment Of Boleta.
Payment Of Boleta */
public int getC_Boleta_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Boleta_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int DOCACTION_AD_Reference_ID=135;
/** Approve = AP */
public static final String DOCACTION_Approve = "AP";
/** Close = CL */
public static final String DOCACTION_Close = "CL";
/** Prepare = PR */
public static final String DOCACTION_Prepare = "PR";
/** Invalidate = IN */
public static final String DOCACTION_Invalidate = "IN";
/** Complete = CO */
public static final String DOCACTION_Complete = "CO";
/** <None> = -- */
public static final String DOCACTION_None = "--";
/** Reverse - Correct = RC */
public static final String DOCACTION_Reverse_Correct = "RC";
/** Reject = RJ */
public static final String DOCACTION_Reject = "RJ";
/** Reverse - Accrual = RA */
public static final String DOCACTION_Reverse_Accrual = "RA";
/** Wait Complete = WC */
public static final String DOCACTION_WaitComplete = "WC";
/** Unlock = XL */
public static final String DOCACTION_Unlock = "XL";
/** Re-activate = RE */
public static final String DOCACTION_Re_Activate = "RE";
/** Post = PO */
public static final String DOCACTION_Post = "PO";
/** Void = VO */
public static final String DOCACTION_Void = "VO";
/** Set Document Action.
The targeted status of the document */
public void setDocAction (String DocAction)
{
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO"));
 else throw new IllegalArgumentException ("DocAction Invalid value - Reference_ID=135 - AP - CL - PR - IN - CO - -- - RC - RJ - RA - WC - XL - RE - PO - VO");
if (DocAction == null) throw new IllegalArgumentException ("DocAction is mandatory");
if (DocAction.length() > 2)
{
log.warning("Length > 2 - truncated");
DocAction = DocAction.substring(0,2);
}
set_Value ("DocAction", DocAction);
}
/** Get Document Action.
The targeted status of the document */
public String getDocAction() 
{
return (String)get_Value("DocAction");
}
public static final int DOCSTATUS_AD_Reference_ID=131;
/** Reversed = RE */
public static final String DOCSTATUS_Reversed = "RE";
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
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus.equals("RE") || DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference_ID=131 - RE - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN");
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
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
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
/** Set Fecha Acreditacion */
public void setFechaAcreditacion (Timestamp FechaAcreditacion)
{
set_Value ("FechaAcreditacion", FechaAcreditacion);
}
/** Get Fecha Acreditacion */
public Timestamp getFechaAcreditacion() 
{
return (Timestamp)get_Value("FechaAcreditacion");
}
/** Set Fecha Deposito */
public void setFechaDeposito (Timestamp FechaDeposito)
{
if (FechaDeposito == null) throw new IllegalArgumentException ("FechaDeposito is mandatory");
set_Value ("FechaDeposito", FechaDeposito);
}
/** Get Fecha Deposito */
public Timestamp getFechaDeposito() 
{
return (Timestamp)get_Value("FechaDeposito");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getFechaDeposito()));
}
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
if (GrandTotal == null) throw new IllegalArgumentException ("GrandTotal is mandatory");
set_Value ("GrandTotal", GrandTotal);
}
/** Get Grand Total.
Total amount of document */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Reconciled.
Payment is reconciled with bank statement */
public void setIsReconciled (boolean IsReconciled)
{
set_Value ("IsReconciled", new Boolean(IsReconciled));
}
/** Get Reconciled.
Payment is reconciled with bank statement */
public boolean isReconciled() 
{
Object oo = get_Value("IsReconciled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Boleta de depósito */
public void setM_BoletaDeposito_ID (int M_BoletaDeposito_ID)
{
set_ValueNoCheck ("M_BoletaDeposito_ID", new Integer(M_BoletaDeposito_ID));
}
/** Get Boleta de depósito */
public int getM_BoletaDeposito_ID() 
{
Integer ii = (Integer)get_Value("M_BoletaDeposito_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Posted.
Posting status */
public void setPosted (boolean Posted)
{
set_Value ("Posted", new Boolean(Posted));
}
/** Get Posted.
Posting status */
public boolean isPosted() 
{
Object oo = get_Value("Posted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
}
