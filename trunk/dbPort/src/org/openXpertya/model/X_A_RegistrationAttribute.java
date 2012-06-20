/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por A_RegistrationAttribute
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.843 */
public class X_A_RegistrationAttribute extends PO
{
/** Constructor estándar */
public X_A_RegistrationAttribute (Properties ctx, int A_RegistrationAttribute_ID, String trxName)
{
super (ctx, A_RegistrationAttribute_ID, trxName);
/** if (A_RegistrationAttribute_ID == 0)
{
setAD_Reference_ID (0);
setA_RegistrationAttribute_ID (0);
setIsSelfService (true);	// Y
setName (null);
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_A_RegistrationAttribute (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=652 */
public static final int Table_ID=652;

/** TableName=A_RegistrationAttribute */
public static final String Table_Name="A_RegistrationAttribute";

protected static KeyNamePair Model = new KeyNamePair(652,"A_RegistrationAttribute");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_A_RegistrationAttribute[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_REFERENCE_ID_AD_Reference_ID=1;
/** Set Reference.
System Reference (Pick List) */
public void setAD_Reference_ID (int AD_Reference_ID)
{
set_Value ("AD_Reference_ID", new Integer(AD_Reference_ID));
}
/** Get Reference.
System Reference (Pick List) */
public int getAD_Reference_ID() 
{
Integer ii = (Integer)get_Value("AD_Reference_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_REFERENCE_VALUE_ID_AD_Reference_ID=4;
/** Set Reference Key.
Required to specify, if data type is Table or List */
public void setAD_Reference_Value_ID (int AD_Reference_Value_ID)
{
if (AD_Reference_Value_ID <= 0) set_Value ("AD_Reference_Value_ID", null);
 else 
set_Value ("AD_Reference_Value_ID", new Integer(AD_Reference_Value_ID));
}
/** Get Reference Key.
Required to specify, if data type is Table or List */
public int getAD_Reference_Value_ID() 
{
Integer ii = (Integer)get_Value("AD_Reference_Value_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set DB Column Name.
Name of the column in the database */
public void setColumnName (String ColumnName)
{
if (ColumnName != null && ColumnName.length() > 40)
{
log.warning("Length > 40 - truncated");
ColumnName = ColumnName.substring(0,39);
}
set_Value ("ColumnName", ColumnName);
}
/** Get DB Column Name.
Name of the column in the database */
public String getColumnName() 
{
return (String)get_Value("ColumnName");
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
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
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
