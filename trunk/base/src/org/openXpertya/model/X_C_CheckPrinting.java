/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CheckPrinting
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-04-26 14:45:44.988 */
public class X_C_CheckPrinting extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CheckPrinting (Properties ctx, int C_CheckPrinting_ID, String trxName)
{
super (ctx, C_CheckPrinting_ID, trxName);
/** if (C_CheckPrinting_ID == 0)
{
setC_BankAccount_ID (0);
setC_Checkprinting_ID (0);
setC_DocType_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));	// @#Date@
}
 */
}
/** Load Constructor */
public X_C_CheckPrinting (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CheckPrinting");

/** TableName=C_CheckPrinting */
public static final String Table_Name="C_CheckPrinting";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CheckPrinting");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CheckPrinting[").append(getID()).append("]");
return sb.toString();
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
/** Set C_Checkprinting_ID */
public void setC_Checkprinting_ID (int C_Checkprinting_ID)
{
set_ValueNoCheck ("C_Checkprinting_ID", new Integer(C_Checkprinting_ID));
}
/** Get C_Checkprinting_ID */
public int getC_Checkprinting_ID() 
{
Integer ii = (Integer)get_Value("C_Checkprinting_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("Check Printing Document Types");
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
/** Set GetLines */
public void setGetLines (String GetLines)
{
if (GetLines != null && GetLines.length() > 1)
{
log.warning("Length > 1 - truncated");
GetLines = GetLines.substring(0,1);
}
set_Value ("GetLines", GetLines);
}
/** Get GetLines */
public String getGetLines() 
{
return (String)get_Value("GetLines");
}
/** Set MarkPrinted */
public void setMarkPrinted (String MarkPrinted)
{
if (MarkPrinted != null && MarkPrinted.length() > 1)
{
log.warning("Length > 1 - truncated");
MarkPrinted = MarkPrinted.substring(0,1);
}
set_Value ("MarkPrinted", MarkPrinted);
}
/** Get MarkPrinted */
public String getMarkPrinted() 
{
return (String)get_Value("MarkPrinted");
}
}
