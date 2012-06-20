/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_Achievement
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.093 */
public class X_PA_Achievement extends PO
{
/** Constructor estándar */
public X_PA_Achievement (Properties ctx, int PA_Achievement_ID, String trxName)
{
super (ctx, PA_Achievement_ID, trxName);
/** if (PA_Achievement_ID == 0)
{
setAD_User_ID (0);
setIsAchieved (false);
setIsSummary (false);
setName (null);
setPA_Achievement_ID (0);
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_PA_Achievement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=438 */
public static final int Table_ID=438;

/** TableName=PA_Achievement */
public static final String Table_Name="PA_Achievement";

protected static KeyNamePair Model = new KeyNamePair(438,"PA_Achievement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_Achievement[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Note.
Additional Information about  an achievement */
public void setAchiveNote (String AchiveNote)
{
if (AchiveNote != null && AchiveNote.length() > 2000)
{
log.warning("Length > 2000 - truncated");
AchiveNote = AchiveNote.substring(0,1999);
}
set_Value ("AchiveNote", AchiveNote);
}
/** Get Note.
Additional Information about  an achievement */
public String getAchiveNote() 
{
return (String)get_Value("AchiveNote");
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
/** Set Achieved.
The goal is achieved */
public void setIsAchieved (boolean IsAchieved)
{
set_Value ("IsAchieved", new Boolean(IsAchieved));
}
/** Get Achieved.
The goal is achieved */
public boolean isAchieved() 
{
Object oo = get_Value("IsAchieved");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
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
/** Set Note.
Optional additional user defined information */
public void setNote (String Note)
{
if (Note != null && Note.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Note = Note.substring(0,1999);
}
set_Value ("Note", Note);
}
/** Get Note.
Optional additional user defined information */
public String getNote() 
{
return (String)get_Value("Note");
}
/** Set Achievement.
Performance Achievement */
public void setPA_Achievement_ID (int PA_Achievement_ID)
{
set_ValueNoCheck ("PA_Achievement_ID", new Integer(PA_Achievement_ID));
}
/** Get Achievement.
Performance Achievement */
public int getPA_Achievement_ID() 
{
Integer ii = (Integer)get_Value("PA_Achievement_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PARENT_ID_AD_Reference_ID=229;
/** Set Parent.
Parent of Entity */
public void setParent_ID (int Parent_ID)
{
if (Parent_ID <= 0) set_Value ("Parent_ID", null);
 else 
set_Value ("Parent_ID", new Integer(Parent_ID));
}
/** Get Parent.
Parent of Entity */
public int getParent_ID() 
{
Integer ii = (Integer)get_Value("Parent_ID");
if (ii == null) return 0;
return ii.intValue();
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
