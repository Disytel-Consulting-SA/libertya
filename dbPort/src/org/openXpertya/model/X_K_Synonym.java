/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_Synonym
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.546 */
public class X_K_Synonym extends PO
{
/** Constructor estándar */
public X_K_Synonym (Properties ctx, int K_Synonym_ID, String trxName)
{
super (ctx, K_Synonym_ID, trxName);
/** if (K_Synonym_ID == 0)
{
setAD_Language (null);
setK_Synonym_ID (0);
setName (null);
setSynonymName (null);
}
 */
}
/** Load Constructor */
public X_K_Synonym (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=608 */
public static final int Table_ID=608;

/** TableName=K_Synonym */
public static final String Table_Name="K_Synonym";

protected static KeyNamePair Model = new KeyNamePair(608,"K_Synonym");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_Synonym[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_LANGUAGE_AD_Reference_ID=106;
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language == null) throw new IllegalArgumentException ("AD_Language is mandatory");
if (AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,5);
}
set_Value ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set Knowledge Synonym.
Knowlege Keyword Synonym */
public void setK_Synonym_ID (int K_Synonym_ID)
{
set_ValueNoCheck ("K_Synonym_ID", new Integer(K_Synonym_ID));
}
/** Get Knowledge Synonym.
Knowlege Keyword Synonym */
public int getK_Synonym_ID() 
{
Integer ii = (Integer)get_Value("K_Synonym_ID");
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
/** Set Synonym Name.
The synonym for the name */
public void setSynonymName (String SynonymName)
{
if (SynonymName == null) throw new IllegalArgumentException ("SynonymName is mandatory");
if (SynonymName.length() > 60)
{
log.warning("Length > 60 - truncated");
SynonymName = SynonymName.substring(0,59);
}
set_Value ("SynonymName", SynonymName);
}
/** Get Synonym Name.
The synonym for the name */
public String getSynonymName() 
{
return (String)get_Value("SynonymName");
}
}
