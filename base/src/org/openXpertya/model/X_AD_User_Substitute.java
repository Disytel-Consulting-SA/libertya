/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_User_Substitute
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:28.82 */
public class X_AD_User_Substitute extends PO
{
/** Constructor estándar */
public X_AD_User_Substitute (Properties ctx, int AD_User_Substitute_ID, String trxName)
{
super (ctx, AD_User_Substitute_ID, trxName);
/** if (AD_User_Substitute_ID == 0)
{
setAD_User_ID (0);
setAD_User_Substitute_ID (0);
setName (null);
setSubstitute_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_User_Substitute (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=642 */
public static final int Table_ID=642;

/** TableName=AD_User_Substitute */
public static final String Table_Name="AD_User_Substitute";

protected static KeyNamePair Model = new KeyNamePair(642,"AD_User_Substitute");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_User_Substitute[").append(getID()).append("]");
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
/** Set User Substitute.
Substitute of the user */
public void setAD_User_Substitute_ID (int AD_User_Substitute_ID)
{
set_ValueNoCheck ("AD_User_Substitute_ID", new Integer(AD_User_Substitute_ID));
}
/** Get User Substitute.
Substitute of the user */
public int getAD_User_Substitute_ID() 
{
Integer ii = (Integer)get_Value("AD_User_Substitute_ID");
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
public static final int SUBSTITUTE_ID_AD_Reference_ID=110;
/** Set Substitute.
Entity which can be used in place of this entity */
public void setSubstitute_ID (int Substitute_ID)
{
set_Value ("Substitute_ID", new Integer(Substitute_ID));
}
/** Get Substitute.
Entity which can be used in place of this entity */
public int getSubstitute_ID() 
{
Integer ii = (Integer)get_Value("Substitute_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
