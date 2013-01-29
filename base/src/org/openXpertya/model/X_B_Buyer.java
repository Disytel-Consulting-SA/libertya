/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_Buyer
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.968 */
public class X_B_Buyer extends PO
{
/** Constructor estándar */
public X_B_Buyer (Properties ctx, int B_Buyer_ID, String trxName)
{
super (ctx, B_Buyer_ID, trxName);
/** if (B_Buyer_ID == 0)
{
setAD_User_ID (0);
setName (null);
setValidTo (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_B_Buyer (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=684 */
public static final int Table_ID=684;

/** TableName=B_Buyer */
public static final String Table_Name="B_Buyer";

protected static KeyNamePair Model = new KeyNamePair(684,"B_Buyer");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_Buyer[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
set_ValueNoCheck ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
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
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
if (ValidTo == null) throw new IllegalArgumentException ("ValidTo is mandatory");
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
