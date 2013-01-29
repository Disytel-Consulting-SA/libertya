/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_Source
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.5 */
public class X_K_Source extends PO
{
/** Constructor estándar */
public X_K_Source (Properties ctx, int K_Source_ID, String trxName)
{
super (ctx, K_Source_ID, trxName);
/** if (K_Source_ID == 0)
{
setK_Source_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_K_Source (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=609 */
public static final int Table_ID=609;

/** TableName=K_Source */
public static final String Table_Name="K_Source";

protected static KeyNamePair Model = new KeyNamePair(609,"K_Source");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_Source[").append(getID()).append("]");
return sb.toString();
}
/** Set Description URL.
URL for the description */
public void setDescriptionURL (String DescriptionURL)
{
if (DescriptionURL != null && DescriptionURL.length() > 120)
{
log.warning("Length > 120 - truncated");
DescriptionURL = DescriptionURL.substring(0,119);
}
set_Value ("DescriptionURL", DescriptionURL);
}
/** Get Description URL.
URL for the description */
public String getDescriptionURL() 
{
return (String)get_Value("DescriptionURL");
}
/** Set Knowledge Source.
Source of a Knowledge Entry */
public void setK_Source_ID (int K_Source_ID)
{
set_ValueNoCheck ("K_Source_ID", new Integer(K_Source_ID));
}
/** Get Knowledge Source.
Source of a Knowledge Entry */
public int getK_Source_ID() 
{
Integer ii = (Integer)get_Value("K_Source_ID");
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
}
