/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ElectronicPaymentBranch
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-24 20:12:33.886 */
public class X_C_ElectronicPaymentBranch extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_ElectronicPaymentBranch (Properties ctx, int C_ElectronicPaymentBranch_ID, String trxName)
{
super (ctx, C_ElectronicPaymentBranch_ID, trxName);
/** if (C_ElectronicPaymentBranch_ID == 0)
{
setC_Bank_ID (0);
setC_ElectronicPaymentBranch_ID (0);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_ElectronicPaymentBranch (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_ElectronicPaymentBranch");

/** TableName=C_ElectronicPaymentBranch */
public static final String Table_Name="C_ElectronicPaymentBranch";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_ElectronicPaymentBranch");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ElectronicPaymentBranch[").append(getID()).append("]");
return sb.toString();
}
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
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
/** Set C_ElectronicPaymentBranch_ID */
public void setC_ElectronicPaymentBranch_ID (int C_ElectronicPaymentBranch_ID)
{
set_ValueNoCheck ("C_ElectronicPaymentBranch_ID", new Integer(C_ElectronicPaymentBranch_ID));
}
/** Get C_ElectronicPaymentBranch_ID */
public int getC_ElectronicPaymentBranch_ID() 
{
Integer ii = (Integer)get_Value("C_ElectronicPaymentBranch_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
