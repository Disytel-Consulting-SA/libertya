/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_BoletaDepositoLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-07-08 09:14:26.255 */
public class X_M_BoletaDepositoLine extends PO
{
/** Constructor estándar */
public X_M_BoletaDepositoLine (Properties ctx, int M_BoletaDepositoLine_ID, String trxName)
{
super (ctx, M_BoletaDepositoLine_ID, trxName);
/** if (M_BoletaDepositoLine_ID == 0)
{
setC_Currency_ID (0);
setC_Payment_ID (0);
setIsReconciled (false);	// N
setM_BoletaDeposito_ID (0);	// @M_BoletaDeposito_ID@
setM_BoletaDepositoLine_ID (0);
}
 */
}
/** Load Constructor */
public X_M_BoletaDepositoLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000130 */
public static final int Table_ID=1000130;

/** TableName=M_BoletaDepositoLine */
public static final String Table_Name="M_BoletaDepositoLine";

protected static KeyNamePair Model = new KeyNamePair(1000130,"M_BoletaDepositoLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_BoletaDepositoLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
if (C_BankAccount_ID <= 0) set_Value ("C_BankAccount_ID", null);
 else 
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
public static final int C_DEPO_PAYMENT_ID_AD_Reference_ID=343;
/** Set Depositted Check.
Depositted Check */
public void setC_Depo_Payment_ID (int C_Depo_Payment_ID)
{
if (C_Depo_Payment_ID <= 0) set_Value ("C_Depo_Payment_ID", null);
 else 
set_Value ("C_Depo_Payment_ID", new Integer(C_Depo_Payment_ID));
}
/** Get Depositted Check.
Depositted Check */
public int getC_Depo_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Depo_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
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
public static final int C_REVERSE_PAYMENT_ID_AD_Reference_ID=343;
/** Set Reverse Check of Depositted Check.
Reverse Check of Depositted Check */
public void setC_Reverse_Payment_ID (int C_Reverse_Payment_ID)
{
if (C_Reverse_Payment_ID <= 0) set_Value ("C_Reverse_Payment_ID", null);
 else 
set_Value ("C_Reverse_Payment_ID", new Integer(C_Reverse_Payment_ID));
}
/** Get Reverse Check of Depositted Check.
Reverse Check of Depositted Check */
public int getC_Reverse_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Reverse_Payment_ID");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDescription());
}
/** Set Due Date */
public void setDueDate (Timestamp DueDate)
{
throw new IllegalArgumentException ("DueDate is virtual column");
}
/** Get Due Date */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
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
set_Value ("M_BoletaDeposito_ID", new Integer(M_BoletaDeposito_ID));
}
/** Get Boleta de depósito */
public int getM_BoletaDeposito_ID() 
{
Integer ii = (Integer)get_Value("M_BoletaDeposito_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Linea Boleta Depósito */
public void setM_BoletaDepositoLine_ID (int M_BoletaDepositoLine_ID)
{
set_ValueNoCheck ("M_BoletaDepositoLine_ID", new Integer(M_BoletaDepositoLine_ID));
}
/** Get Linea Boleta Depósito */
public int getM_BoletaDepositoLine_ID() 
{
Integer ii = (Integer)get_Value("M_BoletaDepositoLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Amount */
public void setPayment_Amt (BigDecimal Payment_Amt)
{
set_Value ("Payment_Amt", Payment_Amt);
}
/** Get Payment Amount */
public BigDecimal getPayment_Amt() 
{
BigDecimal bd = (BigDecimal)get_Value("Payment_Amt");
if (bd == null) return Env.ZERO;
return bd;
}
}
