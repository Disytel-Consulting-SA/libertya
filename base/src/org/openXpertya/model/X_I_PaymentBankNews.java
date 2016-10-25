/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_PaymentBankNews
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-25 15:20:07.162 */
public class X_I_PaymentBankNews extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_PaymentBankNews (Properties ctx, int I_PaymentBankNews_ID, String trxName)
{
super (ctx, I_PaymentBankNews_ID, trxName);
/** if (I_PaymentBankNews_ID == 0)
{
setI_IsImported (false);
setI_PaymentBankNews_ID (0);
}
 */
}
/** Load Constructor */
public X_I_PaymentBankNews (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_PaymentBankNews");

/** TableName=I_PaymentBankNews */
public static final String Table_Name="I_PaymentBankNews";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_PaymentBankNews");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_PaymentBankNews[").append(getID()).append("]");
return sb.toString();
}
public static final int C_BANK_ID_AD_Reference_ID = MReference.getReferenceID("C_Bank");
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
if (C_Bank_ID <= 0) set_Value ("C_Bank_ID", null);
 else 
set_Value ("C_Bank_ID", new Integer(C_Bank_ID));
}
/** Get Bank.
Bank */
public int getC_Bank_ID() 
{
Integer ii = (Integer)get_Value("C_Bank_ID");
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
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,2000);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set I_PaymentBankNews_ID */
public void setI_PaymentBankNews_ID (int I_PaymentBankNews_ID)
{
set_ValueNoCheck ("I_PaymentBankNews_ID", new Integer(I_PaymentBankNews_ID));
}
/** Get I_PaymentBankNews_ID */
public int getI_PaymentBankNews_ID() 
{
Integer ii = (Integer)get_Value("I_PaymentBankNews_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set List_Type */
public void setList_Type (String List_Type)
{
if (List_Type != null && List_Type.length() > 1)
{
log.warning("Length > 1 - truncated");
List_Type = List_Type.substring(0,1);
}
set_Value ("List_Type", List_Type);
}
/** Get List_Type */
public String getList_Type() 
{
return (String)get_Value("List_Type");
}
/** Set List_Value */
public void setList_Value (String List_Value)
{
if (List_Value != null && List_Value.length() > 8)
{
log.warning("Length > 8 - truncated");
List_Value = List_Value.substring(0,8);
}
set_Value ("List_Value", List_Value);
}
/** Get List_Value */
public String getList_Value() 
{
return (String)get_Value("List_Value");
}
/** Set Payment_Order */
public void setPayment_Order (String Payment_Order)
{
if (Payment_Order != null && Payment_Order.length() > 25)
{
log.warning("Length > 25 - truncated");
Payment_Order = Payment_Order.substring(0,25);
}
set_Value ("Payment_Order", Payment_Order);
}
/** Get Payment_Order */
public String getPayment_Order() 
{
return (String)get_Value("Payment_Order");
}
/** Set Payment_Status */
public void setPayment_Status (String Payment_Status)
{
if (Payment_Status != null && Payment_Status.length() > 2)
{
log.warning("Length > 2 - truncated");
Payment_Status = Payment_Status.substring(0,2);
}
set_Value ("Payment_Status", Payment_Status);
}
/** Get Payment_Status */
public String getPayment_Status() 
{
return (String)get_Value("Payment_Status");
}
/** Set Payment_Status_Msg */
public void setPayment_Status_Msg (String Payment_Status_Msg)
{
if (Payment_Status_Msg != null && Payment_Status_Msg.length() > 60)
{
log.warning("Length > 60 - truncated");
Payment_Status_Msg = Payment_Status_Msg.substring(0,60);
}
set_Value ("Payment_Status_Msg", Payment_Status_Msg);
}
/** Get Payment_Status_Msg */
public String getPayment_Status_Msg() 
{
return (String)get_Value("Payment_Status_Msg");
}
/** Set Process_Date */
public void setProcess_Date (Timestamp Process_Date)
{
set_Value ("Process_Date", Process_Date);
}
/** Get Process_Date */
public Timestamp getProcess_Date() 
{
return (Timestamp)get_Value("Process_Date");
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
/** Set Receipt_Number */
public void setReceipt_Number (String Receipt_Number)
{
if (Receipt_Number != null && Receipt_Number.length() > 15)
{
log.warning("Length > 15 - truncated");
Receipt_Number = Receipt_Number.substring(0,15);
}
set_Value ("Receipt_Number", Receipt_Number);
}
/** Get Receipt_Number */
public String getReceipt_Number() 
{
return (String)get_Value("Receipt_Number");
}
/** Set Register_Number */
public void setRegister_Number (String Register_Number)
{
if (Register_Number != null && Register_Number.length() > 15)
{
log.warning("Length > 15 - truncated");
Register_Number = Register_Number.substring(0,15);
}
set_Value ("Register_Number", Register_Number);
}
/** Get Register_Number */
public String getRegister_Number() 
{
return (String)get_Value("Register_Number");
}
}
