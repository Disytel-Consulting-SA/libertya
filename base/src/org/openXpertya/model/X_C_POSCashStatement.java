/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_POSCashStatement
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-02-16 17:04:42.418 */
public class X_C_POSCashStatement extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_POSCashStatement (Properties ctx, int C_POSCashStatement_ID, String trxName)
{
super (ctx, C_POSCashStatement_ID, trxName);
/** if (C_POSCashStatement_ID == 0)
{
setAmount (Env.ZERO);
setCashValue (null);
setC_Currency_ID (0);	// @$C_Currency_ID@
setC_POSCashStatement_ID (0);
setC_POSJournal_ID (0);	// @C_POSJournal_ID@
setQty (0);
}
 */
}
/** Load Constructor */
public X_C_POSCashStatement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_POSCashStatement");

/** TableName=C_POSCashStatement */
public static final String Table_Name="C_POSCashStatement";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_POSCashStatement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_POSCashStatement[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
if (Amount == null) throw new IllegalArgumentException ("Amount is mandatory");
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
public static final int CASHVALUE_AD_Reference_ID = MReference.getReferenceID("C_POSCashStatement Cash Value");
/** 5 Cents = C005 */
public static final String CASHVALUE_5Cents = "C005";
/** 10 Cents = C010 */
public static final String CASHVALUE_10Cents = "C010";
/** 25 Cents = C025 */
public static final String CASHVALUE_25Cents = "C025";
/** 50 Cents = C050 */
public static final String CASHVALUE_50Cents = "C050";
/** 5 Units = U005 */
public static final String CASHVALUE_5Units = "U005";
/** 20 Units = U020 */
public static final String CASHVALUE_20Units = "U020";
/** 50 Units = U050 */
public static final String CASHVALUE_50Units = "U050";
/** 100 Units = U100 */
public static final String CASHVALUE_100Units = "U100";
/** 10 Units = U010 */
public static final String CASHVALUE_10Units = "U010";
/** 1 Unit = U001 */
public static final String CASHVALUE_1Unit = "U001";
/** 2 Units = U002 */
public static final String CASHVALUE_2Units = "U002";
/** Set Cash Value.
Cash Value */
public void setCashValue (String CashValue)
{
if (CashValue.equals("C005") || CashValue.equals("C010") || CashValue.equals("C025") || CashValue.equals("C050") || CashValue.equals("U005") || CashValue.equals("U020") || CashValue.equals("U050") || CashValue.equals("U100") || CashValue.equals("U010") || CashValue.equals("U001") || CashValue.equals("U002"));
 else throw new IllegalArgumentException ("CashValue Invalid value - Reference = CASHVALUE_AD_Reference_ID - C005 - C010 - C025 - C050 - U005 - U020 - U050 - U100 - U010 - U001 - U002");
if (CashValue == null) throw new IllegalArgumentException ("CashValue is mandatory");
if (CashValue.length() > 4)
{
log.warning("Length > 4 - truncated");
CashValue = CashValue.substring(0,4);
}
set_Value ("CashValue", CashValue);
}
/** Get Cash Value.
Cash Value */
public String getCashValue() 
{
return (String)get_Value("CashValue");
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
/** Set POS Cash Statement.
POS Cash Statement */
public void setC_POSCashStatement_ID (int C_POSCashStatement_ID)
{
set_ValueNoCheck ("C_POSCashStatement_ID", new Integer(C_POSCashStatement_ID));
}
/** Get POS Cash Statement.
POS Cash Statement */
public int getC_POSCashStatement_ID() 
{
Integer ii = (Integer)get_Value("C_POSCashStatement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Journal.
POS Journal */
public void setC_POSJournal_ID (int C_POSJournal_ID)
{
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
/** Set Quantity.
Quantity */
public void setQty (int Qty)
{
set_Value ("Qty", new Integer(Qty));
}
/** Get Quantity.
Quantity */
public int getQty() 
{
Integer ii = (Integer)get_Value("Qty");
if (ii == null) return 0;
return ii.intValue();
}
}
