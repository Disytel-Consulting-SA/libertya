/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankListLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-05-23 10:58:59.491 */
public class X_C_BankListLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankListLine (Properties ctx, int C_BankListLine_ID, String trxName)
{
super (ctx, C_BankListLine_ID, trxName);
/** if (C_BankListLine_ID == 0)
{
setC_BankList_ID (0);
setC_BankListLine_ID (0);
setC_Payment_ID (0);
setLine (Env.ZERO);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_BankListLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankListLine");

/** TableName=C_BankListLine */
public static final String Table_Name="C_BankListLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankListLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankListLine[").append(getID()).append("]");
return sb.toString();
}
/** Set C_BankList_ID */
public void setC_BankList_ID (int C_BankList_ID)
{
set_Value ("C_BankList_ID", new Integer(C_BankList_ID));
}
/** Get C_BankList_ID */
public int getC_BankList_ID() 
{
Integer ii = (Integer)get_Value("C_BankList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_BankListLine_ID */
public void setC_BankListLine_ID (int C_BankListLine_ID)
{
set_ValueNoCheck ("C_BankListLine_ID", new Integer(C_BankListLine_ID));
}
/** Get C_BankListLine_ID */
public int getC_BankListLine_ID() 
{
Integer ii = (Integer)get_Value("C_BankListLine_ID");
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
/** Set Line No.
Unique line for this document */
public void setLine (BigDecimal Line)
{
if (Line == null) throw new IllegalArgumentException ("Line is mandatory");
set_Value ("Line", Line);
}
/** Get Line No.
Unique line for this document */
public BigDecimal getLine() 
{
BigDecimal bd = (BigDecimal)get_Value("Line");
if (bd == null) return Env.ZERO;
return bd;
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
