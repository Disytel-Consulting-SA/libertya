/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por A_RegistrationValue
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.906 */
public class X_A_RegistrationValue extends PO
{
/** Constructor estándar */
public X_A_RegistrationValue (Properties ctx, int A_RegistrationValue_ID, String trxName)
{
super (ctx, A_RegistrationValue_ID, trxName);
/** if (A_RegistrationValue_ID == 0)
{
setA_RegistrationAttribute_ID (0);
setA_Registration_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_A_RegistrationValue (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=653 */
public static final int Table_ID=653;

/** TableName=A_RegistrationValue */
public static final String Table_Name="A_RegistrationValue";

protected static KeyNamePair Model = new KeyNamePair(653,"A_RegistrationValue");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_A_RegistrationValue[").append(getID()).append("]");
return sb.toString();
}
/** Set Registration Attribute.
Asset Registration Attribute */
public void setA_RegistrationAttribute_ID (int A_RegistrationAttribute_ID)
{
set_ValueNoCheck ("A_RegistrationAttribute_ID", new Integer(A_RegistrationAttribute_ID));
}
/** Get Registration Attribute.
Asset Registration Attribute */
public int getA_RegistrationAttribute_ID() 
{
Integer ii = (Integer)get_Value("A_RegistrationAttribute_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getA_RegistrationAttribute_ID()));
}
/** Set Registration.
User Asset Registration */
public void setA_Registration_ID (int A_Registration_ID)
{
set_ValueNoCheck ("A_Registration_ID", new Integer(A_Registration_ID));
}
/** Get Registration.
User Asset Registration */
public int getA_Registration_ID() 
{
Integer ii = (Integer)get_Value("A_Registration_ID");
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
}
