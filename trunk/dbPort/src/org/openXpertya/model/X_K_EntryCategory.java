/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_EntryCategory
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.468 */
public class X_K_EntryCategory extends PO
{
/** Constructor estándar */
public X_K_EntryCategory (Properties ctx, int K_EntryCategory_ID, String trxName)
{
super (ctx, K_EntryCategory_ID, trxName);
/** if (K_EntryCategory_ID == 0)
{
setK_CategoryValue_ID (0);
setK_Category_ID (0);
setK_Entry_ID (0);
}
 */
}
/** Load Constructor */
public X_K_EntryCategory (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=611 */
public static final int Table_ID=611;

/** TableName=K_EntryCategory */
public static final String Table_Name="K_EntryCategory";

protected static KeyNamePair Model = new KeyNamePair(611,"K_EntryCategory");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_EntryCategory[").append(getID()).append("]");
return sb.toString();
}
/** Set Category Value.
The value of the category */
public void setK_CategoryValue_ID (int K_CategoryValue_ID)
{
set_Value ("K_CategoryValue_ID", new Integer(K_CategoryValue_ID));
}
/** Get Category Value.
The value of the category */
public int getK_CategoryValue_ID() 
{
Integer ii = (Integer)get_Value("K_CategoryValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getK_CategoryValue_ID()));
}
/** Set Knowledge Category.
Knowledge Category */
public void setK_Category_ID (int K_Category_ID)
{
set_Value ("K_Category_ID", new Integer(K_Category_ID));
}
/** Get Knowledge Category.
Knowledge Category */
public int getK_Category_ID() 
{
Integer ii = (Integer)get_Value("K_Category_ID");
if (ii == null) return 0;
return ii.intValue();
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
}
