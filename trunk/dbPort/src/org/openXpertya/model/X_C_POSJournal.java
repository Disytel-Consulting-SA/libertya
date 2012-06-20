/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_POSJournal
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-02-16 18:36:33.711 */
public class X_C_POSJournal extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_POSJournal (Properties ctx, int C_POSJournal_ID, String trxName)
{
super (ctx, C_POSJournal_ID, trxName);
/** if (C_POSJournal_ID == 0)
{
setAD_User_ID (0);	// -1
setCashStatementAmt (Env.ZERO);
setC_POS_ID (0);
setC_POSJournal_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDocAction (null);	// PR
setDocStatus (null);	// DR
setProcessed (false);	// N
}
 */
}
/** Load Constructor */
public X_C_POSJournal (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_POSJournal");

/** TableName=C_POSJournal */
public static final String Table_Name="C_POSJournal";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_POSJournal");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_POSJournal[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_USER_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - SalesRep");
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
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
/** Set Cash Balance */
public void setCashBalance (BigDecimal CashBalance)
{
throw new IllegalArgumentException ("CashBalance is virtual column");
}
/** Get Cash Balance */
public BigDecimal getCashBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("CashBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cash Statement Amount.
Cash Statement Amount */
public void setCashStatementAmt (BigDecimal CashStatementAmt)
{
if (CashStatementAmt == null) throw new IllegalArgumentException ("CashStatementAmt is mandatory");
set_Value ("CashStatementAmt", CashStatementAmt);
}
/** Get Cash Statement Amount.
Cash Statement Amount */
public BigDecimal getCashStatementAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CashStatementAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cash Transfer.
Cash Transfer */
public void setCashTransfer (String CashTransfer)
{
if (CashTransfer != null && CashTransfer.length() > 1)
{
log.warning("Length > 1 - truncated");
CashTransfer = CashTransfer.substring(0,1);
}
set_Value ("CashTransfer", CashTransfer);
}
/** Get Cash Transfer.
Cash Transfer */
public String getCashTransfer() 
{
return (String)get_Value("CashTransfer");
}
/** Set Cash Journal.
Cash Journal */
public void setC_Cash_ID (int C_Cash_ID)
{
if (C_Cash_ID <= 0) set_Value ("C_Cash_ID", null);
 else 
set_Value ("C_Cash_ID", new Integer(C_Cash_ID));
}
/** Get Cash Journal.
Cash Journal */
public int getC_Cash_ID() 
{
Integer ii = (Integer)get_Value("C_Cash_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CASHTARGET_ID_AD_Reference_ID = MReference.getReferenceID("C_Cash");
/** Set Target Cash.
Target Cash */
public void setC_CashTarget_ID (int C_CashTarget_ID)
{
if (C_CashTarget_ID <= 0) set_Value ("C_CashTarget_ID", null);
 else 
set_Value ("C_CashTarget_ID", new Integer(C_CashTarget_ID));
}
/** Get Target Cash.
Target Cash */
public int getC_CashTarget_ID() 
{
Integer ii = (Integer)get_Value("C_CashTarget_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Terminal.
Point of Sales Terminal */
public void setC_POS_ID (int C_POS_ID)
{
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_POS_ID()));
}
/** Set POS Journal.
POS Journal */
public void setC_POSJournal_ID (int C_POSJournal_ID)
{
set_ValueNoCheck ("C_POSJournal_ID", new Integer(C_POSJournal_ID));
}
/** Get POS Journal.
POS Journal */
public int getC_POSJournal_ID() 
{
Integer ii = (Integer)get_Value("C_POSJournal_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int DOCACTION_AD_Reference_ID = MReference.getReferenceID("C_POSJournal Actions");
/** Close = CL */
public static final String DOCACTION_Close = "CL";
/** Complete = CO */
public static final String DOCACTION_Complete = "CO";
/** <None> = -- */
public static final String DOCACTION_None = "--";
/** Open = PR */
public static final String DOCACTION_Open = "PR";
/** Set Document Action.
The targeted status of the document */
public void setDocAction (String DocAction)
{
if (DocAction.equals("CL") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("PR"));
 else throw new IllegalArgumentException ("DocAction Invalid value - Reference = DOCACTION_AD_Reference_ID - CL - CO - -- - PR");
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
public static final int DOCSTATUS_AD_Reference_ID = MReference.getReferenceID("C_POSJournal Status");
/** Closed = CL */
public static final String DOCSTATUS_Closed = "CL";
/** Completed = CO */
public static final String DOCSTATUS_Completed = "CO";
/** Drafted = DR */
public static final String DOCSTATUS_Drafted = "DR";
/** Opened = IP */
public static final String DOCSTATUS_Opened = "IP";
/** Invalid = IN */
public static final String DOCSTATUS_Invalid = "IN";
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus.equals("CL") || DocStatus.equals("CO") || DocStatus.equals("DR") || DocStatus.equals("IP") || DocStatus.equals("IN"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference = DOCSTATUS_AD_Reference_ID - CL - CO - DR - IP - IN");
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
}
