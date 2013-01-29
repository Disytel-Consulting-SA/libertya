/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Job
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:31.745 */
public class X_C_Job extends PO
{
/** Constructor estándar */
public X_C_Job (Properties ctx, int C_Job_ID, String trxName)
{
super (ctx, C_Job_ID, trxName);
/** if (C_Job_ID == 0)
{
setC_JobCategory_ID (0);
setC_Job_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Job (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=789 */
public static final int Table_ID=789;

/** TableName=C_Job */
public static final String Table_Name="C_Job";

protected static KeyNamePair Model = new KeyNamePair(789,"C_Job");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Job[").append(getID()).append("]");
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
/** Set Position Category.
Job Position Category */
public void setC_JobCategory_ID (int C_JobCategory_ID)
{
set_Value ("C_JobCategory_ID", new Integer(C_JobCategory_ID));
}
/** Get Position Category.
Job Position Category */
public int getC_JobCategory_ID() 
{
Integer ii = (Integer)get_Value("C_JobCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Position.
Job Position */
public void setC_Job_ID (int C_Job_ID)
{
set_ValueNoCheck ("C_Job_ID", new Integer(C_Job_ID));
}
/** Get Position.
Job Position */
public int getC_Job_ID() 
{
Integer ii = (Integer)get_Value("C_Job_ID");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
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
}
