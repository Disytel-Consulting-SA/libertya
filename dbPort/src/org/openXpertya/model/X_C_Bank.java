/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Bank
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:29.972 */
public class X_C_Bank extends PO
{
/** Constructor estándar */
public X_C_Bank (Properties ctx, int C_Bank_ID, String trxName)
{
super (ctx, C_Bank_ID, trxName);
/** if (C_Bank_ID == 0)
{
setC_Bank_ID (0);
setIsOwnBank (true);	// Y
setName (null);
setRoutingNo (null);
}
 */
}
/** Load Constructor */
public X_C_Bank (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=296 */
public static final int Table_ID=296;

/** TableName=C_Bank */
public static final String Table_Name="C_Bank";

protected static KeyNamePair Model = new KeyNamePair(296,"C_Bank");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Bank[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
set_ValueNoCheck ("C_Bank_ID", new Integer(C_Bank_ID));
}
/** Get Bank.
Bank */
public int getC_Bank_ID() 
{
Integer ii = (Integer)get_Value("C_Bank_ID");
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
/** Set Own Bank.
Bank for this Organization */
public void setIsOwnBank (boolean IsOwnBank)
{
set_Value ("IsOwnBank", new Boolean(IsOwnBank));
}
/** Get Own Bank.
Bank for this Organization */
public boolean isOwnBank() 
{
Object oo = get_Value("IsOwnBank");
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
/** Set Routing No.
Bank Routing Number */
public void setRoutingNo (String RoutingNo)
{
if (RoutingNo == null) throw new IllegalArgumentException ("RoutingNo is mandatory");
if (RoutingNo.length() > 20)
{
log.warning("Length > 20 - truncated");
RoutingNo = RoutingNo.substring(0,20);
}
set_Value ("RoutingNo", RoutingNo);
}
/** Get Routing No.
Bank Routing Number */
public String getRoutingNo() 
{
return (String)get_Value("RoutingNo");
}
/** Set Swift code.
Swift Code or BIC */
public void setSwiftCode (String SwiftCode)
{
if (SwiftCode != null && SwiftCode.length() > 20)
{
log.warning("Length > 20 - truncated");
SwiftCode = SwiftCode.substring(0,20);
}
set_Value ("SwiftCode", SwiftCode);
}
/** Get Swift code.
Swift Code or BIC */
public String getSwiftCode() 
{
return (String)get_Value("SwiftCode");
}
}
