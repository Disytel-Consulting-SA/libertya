/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_ClickCount
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.437 */
public class X_W_ClickCount extends PO
{
/** Constructor estándar */
public X_W_ClickCount (Properties ctx, int W_ClickCount_ID, String trxName)
{
super (ctx, W_ClickCount_ID, trxName);
/** if (W_ClickCount_ID == 0)
{
setName (null);
setTargetURL (null);
setW_ClickCount_ID (0);
}
 */
}
/** Load Constructor */
public X_W_ClickCount (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=553 */
public static final int Table_ID=553;

/** TableName=W_ClickCount */
public static final String Table_Name="W_ClickCount";

protected static KeyNamePair Model = new KeyNamePair(553,"W_ClickCount");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_ClickCount[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Counter.
Count Value */
public void setCounter (int Counter)
{
throw new IllegalArgumentException ("Counter is virtual column");
}
/** Get Counter.
Count Value */
public int getCounter() 
{
Integer ii = (Integer)get_Value("Counter");
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
/** Set Target URL.
URL for the Target */
public void setTargetURL (String TargetURL)
{
if (TargetURL == null) throw new IllegalArgumentException ("TargetURL is mandatory");
if (TargetURL.length() > 120)
{
log.warning("Length > 120 - truncated");
TargetURL = TargetURL.substring(0,119);
}
set_Value ("TargetURL", TargetURL);
}
/** Get Target URL.
URL for the Target */
public String getTargetURL() 
{
return (String)get_Value("TargetURL");
}
/** Set Click Count.
Web Click Management */
public void setW_ClickCount_ID (int W_ClickCount_ID)
{
set_ValueNoCheck ("W_ClickCount_ID", new Integer(W_ClickCount_ID));
}
/** Get Click Count.
Web Click Management */
public int getW_ClickCount_ID() 
{
Integer ii = (Integer)get_Value("W_ClickCount_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
