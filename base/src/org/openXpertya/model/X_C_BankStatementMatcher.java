/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankStatementMatcher
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.703 */
public class X_C_BankStatementMatcher extends PO
{
/** Constructor estándar */
public X_C_BankStatementMatcher (Properties ctx, int C_BankStatementMatcher_ID, String trxName)
{
super (ctx, C_BankStatementMatcher_ID, trxName);
/** if (C_BankStatementMatcher_ID == 0)
{
setC_BankStatementMatcher_ID (0);
setClassname (null);
setName (null);
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_C_BankStatementMatcher (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=658 */
public static final int Table_ID=658;

/** TableName=C_BankStatementMatcher */
public static final String Table_Name="C_BankStatementMatcher";

protected static KeyNamePair Model = new KeyNamePair(658,"C_BankStatementMatcher");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankStatementMatcher[").append(getID()).append("]");
return sb.toString();
}
/** Set Bank Statement Matcher.
Algorithm to match Bank Statement Info to Business Partners, Invoices and Payments */
public void setC_BankStatementMatcher_ID (int C_BankStatementMatcher_ID)
{
set_ValueNoCheck ("C_BankStatementMatcher_ID", new Integer(C_BankStatementMatcher_ID));
}
/** Get Bank Statement Matcher.
Algorithm to match Bank Statement Info to Business Partners, Invoices and Payments */
public int getC_BankStatementMatcher_ID() 
{
Integer ii = (Integer)get_Value("C_BankStatementMatcher_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Classname.
Java Classname */
public void setClassname (String Classname)
{
if (Classname == null) throw new IllegalArgumentException ("Classname is mandatory");
if (Classname.length() > 60)
{
log.warning("Length > 60 - truncated");
Classname = Classname.substring(0,59);
}
set_Value ("Classname", Classname);
}
/** Get Classname.
Java Classname */
public String getClassname() 
{
return (String)get_Value("Classname");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
