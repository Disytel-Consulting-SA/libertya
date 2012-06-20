/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Sequence
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-30 11:28:20.41 */
public class X_AD_Sequence extends PO
{
/** Constructor estándar */
public X_AD_Sequence (Properties ctx, int AD_Sequence_ID, String trxName)
{
super (ctx, AD_Sequence_ID, trxName);
/** if (AD_Sequence_ID == 0)
{
setAD_Sequence_ID (0);
setCurrentNext (0);	// 1000000
setCurrentNextSys (0);	// 100
setIncrementNo (0);	// 1
setIsAutoSequence (false);
setName (null);
setStartNo (0);	// 1000000
}
 */
}
/** Load Constructor */
public X_AD_Sequence (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=115 */
public static final int Table_ID=115;

/** TableName=AD_Sequence */
public static final String Table_Name="AD_Sequence";

protected static KeyNamePair Model = new KeyNamePair(115,"AD_Sequence");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Sequence[").append(getID()).append("]");
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
/** Set Sequence.
Document Sequence */
public void setAD_Sequence_ID (int AD_Sequence_ID)
{
set_ValueNoCheck ("AD_Sequence_ID", new Integer(AD_Sequence_ID));
}
/** Get Sequence.
Document Sequence */
public int getAD_Sequence_ID() 
{
Integer ii = (Integer)get_Value("AD_Sequence_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Current Next.
The next number to be used */
public void setCurrentNext (int CurrentNext)
{
set_Value ("CurrentNext", new Integer(CurrentNext));
}
/** Get Current Next.
The next number to be used */
public int getCurrentNext() 
{
Integer ii = (Integer)get_Value("CurrentNext");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Current Next (System).
Next sequence for system use */
public void setCurrentNextSys (int CurrentNextSys)
{
set_Value ("CurrentNextSys", new Integer(CurrentNextSys));
}
/** Get Current Next (System).
Next sequence for system use */
public int getCurrentNextSys() 
{
Integer ii = (Integer)get_Value("CurrentNextSys");
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
/** Set Increment.
The number to increment the last document number by */
public void setIncrementNo (int IncrementNo)
{
set_Value ("IncrementNo", new Integer(IncrementNo));
}
/** Get Increment.
The number to increment the last document number by */
public int getIncrementNo() 
{
Integer ii = (Integer)get_Value("IncrementNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Activate Audit.
Activate Audit Trail of what numbers are generated */
public void setIsAudited (boolean IsAudited)
{
set_Value ("IsAudited", new Boolean(IsAudited));
}
/** Get Activate Audit.
Activate Audit Trail of what numbers are generated */
public boolean isAudited() 
{
Object oo = get_Value("IsAudited");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Auto numbering.
Automatically assign the next number */
public void setIsAutoSequence (boolean IsAutoSequence)
{
set_Value ("IsAutoSequence", new Boolean(IsAutoSequence));
}
/** Get Auto numbering.
Automatically assign the next number */
public boolean isAutoSequence() 
{
Object oo = get_Value("IsAutoSequence");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Used for Record ID.
The document number  will be used as the record key */
public void setIsTableID (boolean IsTableID)
{
set_Value ("IsTableID", new Boolean(IsTableID));
}
/** Get Used for Record ID.
The document number  will be used as the record key */
public boolean isTableID() 
{
Object oo = get_Value("IsTableID");
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
/** Set Prefix.
Prefix before the sequence number */
public void setPrefix (String Prefix)
{
if (Prefix != null && Prefix.length() > 10)
{
log.warning("Length > 10 - truncated");
Prefix = Prefix.substring(0,10);
}
set_Value ("Prefix", Prefix);
}
/** Get Prefix.
Prefix before the sequence number */
public String getPrefix() 
{
return (String)get_Value("Prefix");
}
/** Set Restart sequence every Year.
Restart the sequence with Start on every 1/1 */
public void setStartNewYear (boolean StartNewYear)
{
set_Value ("StartNewYear", new Boolean(StartNewYear));
}
/** Get Restart sequence every Year.
Restart the sequence with Start on every 1/1 */
public boolean isStartNewYear() 
{
Object oo = get_Value("StartNewYear");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Start No.
Starting number/position */
public void setStartNo (int StartNo)
{
set_Value ("StartNo", new Integer(StartNo));
}
/** Get Start No.
Starting number/position */
public int getStartNo() 
{
Integer ii = (Integer)get_Value("StartNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Suffix.
Suffix after the number */
public void setSuffix (String Suffix)
{
if (Suffix != null && Suffix.length() > 10)
{
log.warning("Length > 10 - truncated");
Suffix = Suffix.substring(0,10);
}
set_Value ("Suffix", Suffix);
}
/** Get Suffix.
Suffix after the number */
public String getSuffix() 
{
return (String)get_Value("Suffix");
}
/** Set Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setVFormat (String VFormat)
{
if (VFormat != null && VFormat.length() > 40)
{
log.warning("Length > 40 - truncated");
VFormat = VFormat.substring(0,40);
}
set_Value ("VFormat", VFormat);
}
/** Get Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public String getVFormat() 
{
return (String)get_Value("VFormat");
}
}
