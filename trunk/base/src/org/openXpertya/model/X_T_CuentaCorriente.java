/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_CuentaCorriente
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-02-07 02:05:55.566 */
public class X_T_CuentaCorriente extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_CuentaCorriente (Properties ctx, int T_CuentaCorriente_ID, String trxName)
{
super (ctx, T_CuentaCorriente_ID, trxName);
/** if (T_CuentaCorriente_ID == 0)
{
setAD_PInstance_ID (0);
setDateAcct (new Timestamp(System.currentTimeMillis()));
setDateCreated (new Timestamp(System.currentTimeMillis()));
setDateTrx (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_T_CuentaCorriente (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_CuentaCorriente");

/** TableName=T_CuentaCorriente */
public static final String Table_Name="T_CuentaCorriente";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_CuentaCorriente");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_CuentaCorriente[").append(getID()).append("]");
return sb.toString();
}
/** Set Account Type.
Indicates the type of account */
public void setAccountType (String AccountType)
{
if (AccountType != null && AccountType.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountType = AccountType.substring(0,1);
}
set_Value ("AccountType", AccountType);
}
/** Get Account Type.
Indicates the type of account */
public String getAccountType() 
{
return (String)get_Value("AccountType");
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
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
set_Value ("Amount", Amount);
}
/** Get Amount.
Amount in a defined currency */
public BigDecimal getAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
if (C_AllocationHdr_ID <= 0) set_Value ("C_AllocationHdr_ID", null);
 else 
set_Value ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
}
/** Get Allocation.
Payment allocation */
public int getC_AllocationHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (BigDecimal C_BPartner_ID)
{
set_Value ("C_BPartner_ID", C_BPartner_ID);
}
/** Get Business Partner .
Identifies a Business Partner */
public BigDecimal getC_BPartner_ID() 
{
BigDecimal bd = (BigDecimal)get_Value("C_BPartner_ID");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cash Journal Line.
Cash Journal Line */
public void setC_CashLine_ID (int C_CashLine_ID)
{
if (C_CashLine_ID <= 0) set_Value ("C_CashLine_ID", null);
 else 
set_Value ("C_CashLine_ID", new Integer(C_CashLine_ID));
}
/** Get Cash Journal Line.
Cash Journal Line */
public int getC_CashLine_ID() 
{
Integer ii = (Integer)get_Value("C_CashLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
if (C_DocType_ID <= 0) set_Value ("C_DocType_ID", null);
 else 
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
/** Set C_Letracomprobante_ID */
public void setC_Letracomprobante_ID (BigDecimal C_Letracomprobante_ID)
{
set_Value ("C_Letracomprobante_ID", C_Letracomprobante_ID);
}
/** Get C_Letracomprobante_ID */
public BigDecimal getC_Letracomprobante_ID() 
{
BigDecimal bd = (BigDecimal)get_Value("C_Letracomprobante_ID");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_Value ("C_Payment_ID", null);
 else 
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
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
if (DateAcct == null) throw new IllegalArgumentException ("DateAcct is mandatory");
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set DateCreated */
public void setDateCreated (Timestamp DateCreated)
{
if (DateCreated == null) throw new IllegalArgumentException ("DateCreated is mandatory");
set_Value ("DateCreated", DateCreated);
}
/** Get DateCreated */
public Timestamp getDateCreated() 
{
return (Timestamp)get_Value("DateCreated");
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
/** Set debe */
public void setdebe (BigDecimal debe)
{
set_Value ("debe", debe);
}
/** Get debe */
public BigDecimal getdebe() 
{
BigDecimal bd = (BigDecimal)get_Value("debe");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due Date.
Date when the payment is due */
public void setDueDate (Timestamp DueDate)
{
set_Value ("DueDate", DueDate);
}
/** Get Due Date.
Date when the payment is due */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
}
/** Set haber */
public void sethaber (BigDecimal haber)
{
set_Value ("haber", haber);
}
/** Get haber */
public BigDecimal gethaber() 
{
BigDecimal bd = (BigDecimal)get_Value("haber");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set includeopenorders */
public void setincludeopenorders (String includeopenorders)
{
if (includeopenorders != null && includeopenorders.length() > 1)
{
log.warning("Length > 1 - truncated");
includeopenorders = includeopenorders.substring(0,1);
}
set_Value ("includeopenorders", includeopenorders);
}
/** Get includeopenorders */
public String getincludeopenorders() 
{
return (String)get_Value("includeopenorders");
}
/** Set ISO Currency Code.
Three letter ISO 4217 Code of the Currency */
public void setISO_Code (String ISO_Code)
{
if (ISO_Code != null && ISO_Code.length() > 3)
{
log.warning("Length > 3 - truncated");
ISO_Code = ISO_Code.substring(0,3);
}
set_Value ("ISO_Code", ISO_Code);
}
/** Get ISO Currency Code.
Three letter ISO 4217 Code of the Currency */
public String getISO_Code() 
{
return (String)get_Value("ISO_Code");
}
/** Set Receipt.
This is a sales transaction (receipt) */
public void setIsReceipt (boolean IsReceipt)
{
set_Value ("IsReceipt", new Boolean(IsReceipt));
}
/** Get Receipt.
This is a sales transaction (receipt) */
public boolean isReceipt() 
{
Object oo = get_Value("IsReceipt");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Numero Comprobante */
public void setNumeroComprobante (String NumeroComprobante)
{
if (NumeroComprobante != null && NumeroComprobante.length() > 60)
{
log.warning("Length > 60 - truncated");
NumeroComprobante = NumeroComprobante.substring(0,60);
}
set_Value ("NumeroComprobante", NumeroComprobante);
}
/** Get Numero Comprobante */
public String getNumeroComprobante() 
{
return (String)get_Value("NumeroComprobante");
}
/** Set Saldo */
public void setsaldo (BigDecimal saldo)
{
set_Value ("saldo", saldo);
}
/** Get Saldo */
public BigDecimal getsaldo() 
{
BigDecimal bd = (BigDecimal)get_Value("saldo");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set showdetailedreceiptspayments */
public void setshowdetailedreceiptspayments (String showdetailedreceiptspayments)
{
if (showdetailedreceiptspayments != null && showdetailedreceiptspayments.length() > 1)
{
log.warning("Length > 1 - truncated");
showdetailedreceiptspayments = showdetailedreceiptspayments.substring(0,1);
}
set_Value ("showdetailedreceiptspayments", showdetailedreceiptspayments);
}
/** Get showdetailedreceiptspayments */
public String getshowdetailedreceiptspayments() 
{
return (String)get_Value("showdetailedreceiptspayments");
}
/** Set subindice */
public void setsubindice (BigDecimal subindice)
{
set_Value ("subindice", subindice);
}
/** Get subindice */
public BigDecimal getsubindice() 
{
BigDecimal bd = (BigDecimal)get_Value("subindice");
if (bd == null) return Env.ZERO;
return bd;
}
}
