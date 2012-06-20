/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_EntryRelated
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.484 */
public class X_K_EntryRelated extends PO
{
/** Constructor estándar */
public X_K_EntryRelated (Properties ctx, int K_EntryRelated_ID, String trxName)
{
super (ctx, K_EntryRelated_ID, trxName);
/** if (K_EntryRelated_ID == 0)
{
setK_EntryRelated_ID (0);
setK_Entry_ID (0);
}
 */
}
/** Load Constructor */
public X_K_EntryRelated (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=610 */
public static final int Table_ID=610;

/** TableName=K_EntryRelated */
public static final String Table_Name="K_EntryRelated";

protected static KeyNamePair Model = new KeyNamePair(610,"K_EntryRelated");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_EntryRelated[").append(getID()).append("]");
return sb.toString();
}
public static final int K_ENTRYRELATED_ID_AD_Reference_ID=285;
/** Set Related Entry.
Related Entry for this Enntry */
public void setK_EntryRelated_ID (int K_EntryRelated_ID)
{
set_ValueNoCheck ("K_EntryRelated_ID", new Integer(K_EntryRelated_ID));
}
/** Get Related Entry.
Related Entry for this Enntry */
public int getK_EntryRelated_ID() 
{
Integer ii = (Integer)get_Value("K_EntryRelated_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getK_EntryRelated_ID()));
}
/** Set Entry.
Knowledge Entry */
public void setK_Entry_ID (int K_Entry_ID)
{
set_ValueNoCheck ("K_Entry_ID", new Integer(K_Entry_ID));
}
/** Get Entry.
Knowledge Entry */
public int getK_Entry_ID() 
{
Integer ii = (Integer)get_Value("K_Entry_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
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
