/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CashBook
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-11-10 15:32:20.281 */
public class X_C_CashBook extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CashBook (Properties ctx, int C_CashBook_ID, String trxName)
{
super (ctx, C_CashBook_ID, trxName);
/** if (C_CashBook_ID == 0)
{
setCashBookType (null);	// 'G'
setC_CashBook_ID (0);
setC_Currency_ID (0);	// SQL=SELECT cb.C_Currency_ID FROM C_CashBook cb INNER JOIN C_Cash c ON (cb.C_CashBook_ID=c.C_CashBook_ID) WHERE c.C_Cash_ID=@C_Cash_ID@
setIsDefault (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_CashBook (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CashBook");

/** TableName=C_CashBook */
public static final String Table_Name="C_CashBook";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CashBook");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CashBook[").append(getID()).append("]");
return sb.toString();
}
public static final int CASHBOOKTYPE_AD_Reference_ID = MReference.getReferenceID("C_CashBook Type");
/** General Cash Book = G */
public static final String CASHBOOKTYPE_GeneralCashBook = "G";
/** Journal Cash Book = J */
public static final String CASHBOOKTYPE_JournalCashBook = "J";
/** Set Cash Book Type.
Cash Book Type */
public void setCashBookType (String CashBookType)
{
if (CashBookType.equals("G") || CashBookType.equals("J"));
 else throw new IllegalArgumentException ("CashBookType Invalid value - Reference = CASHBOOKTYPE_AD_Reference_ID - G - J");
if (CashBookType == null) throw new IllegalArgumentException ("CashBookType is mandatory");
if (CashBookType.length() > 1)
{
log.warning("Length > 1 - truncated");
CashBookType = CashBookType.substring(0,1);
}
set_ValueNoCheck ("CashBookType", CashBookType);
}
/** Get Cash Book Type.
Cash Book Type */
public String getCashBookType() 
{
return (String)get_Value("CashBookType");
}
/** Set Cash Book.
Cash Book for recording petty cash transactions */
public void setC_CashBook_ID (int C_CashBook_ID)
{
set_ValueNoCheck ("C_CashBook_ID", new Integer(C_CashBook_ID));
}
/** Get Cash Book.
Cash Book for recording petty cash transactions */
public int getC_CashBook_ID() 
{
Integer ii = (Integer)get_Value("C_CashBook_ID");
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
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
}
